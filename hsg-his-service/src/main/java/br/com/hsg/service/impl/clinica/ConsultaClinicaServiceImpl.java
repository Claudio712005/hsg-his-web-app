package br.com.hsg.service.impl.clinica;

import br.com.hsg.dao.AgendaMedicaSlotDAO;
import br.com.hsg.dao.ConsultaAnotacaoDAO;
import br.com.hsg.dao.ConsultaDAO;
import br.com.hsg.dao.ConsultaHistoricoDAO;
import br.com.hsg.domain.entity.AgendaMedicaSlot;
import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.entity.ConsultaAnotacao;
import br.com.hsg.domain.entity.ConsultaHistorico;
import br.com.hsg.domain.enums.AcaoConsulta;
import br.com.hsg.domain.enums.CategoriaNotificacao;
import br.com.hsg.domain.enums.StatusConsulta;
import br.com.hsg.domain.enums.TipoDestinatarioNotificacao;
import br.com.hsg.domain.enums.TipoNotificacao;
import br.com.hsg.domain.enums.TipoResponsavel;
import br.com.hsg.service.facade.clinica.ConsultaClinicaServiceFacade;
import br.com.hsg.service.impl.notificacao.NotificacaoEmissor;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class ConsultaClinicaServiceImpl implements ConsultaClinicaServiceFacade {

    private static final Logger LOG = Logger.getLogger(ConsultaClinicaServiceImpl.class.getName());
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @EJB private ConsultaDAO            consultaDAO;
    @EJB private ConsultaHistoricoDAO   historicoDAO;
    @EJB private ConsultaAnotacaoDAO    anotacaoDAO;
    @EJB private AgendaMedicaSlotDAO    agendaMedicaSlotDAO;
    @EJB private NotificacaoEmissor     emissor;

    @Override
    public List<Consulta> listarConsultasDoDia(LocalDate dia, Long idMedicoFiltro) {
        if (dia == null) {
            throw new IllegalArgumentException("A data é obrigatória.");
        }
        LocalDateTime ini = dia.atStartOfDay();
        LocalDateTime fim = dia.plusDays(1).atStartOfDay();
        return consultaDAO.listarDoDia(ini, fim, idMedicoFiltro);
    }

    @Override
    public List<Consulta> listarConsultasDoDiaMedico(Long idMedico, LocalDate dia) {
        if (idMedico == null) {
            throw new IllegalArgumentException("O médico é obrigatório.");
        }
        return listarConsultasDoDia(dia, idMedico);
    }

    @Override
    public List<Consulta> listarConsultasPorPeriodo(LocalDate inicio, LocalDate fim,
                                                     Long idMedicoFiltro, StatusConsulta status,
                                                     String termoPaciente, Long idEspecialidade) {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("As datas de início e fim são obrigatórias.");
        }
        if (fim.isBefore(inicio)) {
            throw new IllegalArgumentException("A data final deve ser posterior ou igual à data inicial.");
        }
        LocalDateTime ini = inicio.atStartOfDay();
        LocalDateTime fimDt = fim.plusDays(1).atStartOfDay();
        return consultaDAO.listarPorPeriodo(ini, fimDt, idMedicoFiltro, status, termoPaciente, idEspecialidade);
    }

    @Override
    public List<Consulta> listarConsultasMedicoPorPeriodo(Long idMedico, LocalDate inicio, LocalDate fim,
                                                            StatusConsulta status, String termoPaciente) {
        if (idMedico == null) {
            throw new IllegalArgumentException("O médico é obrigatório.");
        }
        return listarConsultasPorPeriodo(inicio, fim, idMedico, status, termoPaciente, null);
    }

    @Override
    public void confirmarChegada(Long idConsulta, Long idResponsavel, TipoResponsavel tipoResponsavel) {
        if (tipoResponsavel == null) {
            throw new IllegalArgumentException("O tipo do responsável é obrigatório.");
        }
        if (tipoResponsavel != TipoResponsavel.ENFERMEIRO && tipoResponsavel != TipoResponsavel.ADMIN) {
            throw new IllegalStateException("Apenas enfermeiros ou administradores podem registrar check-in.");
        }
        Consulta c = requererConsulta(idConsulta);
        if (c.getStatus() != StatusConsulta.AGENDADA) {
            throw new IllegalStateException("Somente consultas com status AGENDADA podem ser confirmadas na recepção.");
        }
        c.confirmar();
        consultaDAO.atualizar(c);
        registrarHistorico(c, AcaoConsulta.CHECK_IN, idResponsavel, tipoResponsavel, null);
        LOG.info("[ConsultaClinicaServiceImpl] Check-in registrado: consulta=" + idConsulta
                + ", responsavel=" + idResponsavel + "/" + tipoResponsavel);

        notificarMedico(c,
                "Paciente chegou",
                "Recepção confirmou a chegada de " + nomePaciente(c)
                        + " para a consulta de " + horaFormatada(c) + ".",
                TipoNotificacao.INFO);
    }

    @Override
    public void marcarRealizadaComObservacao(Long idConsulta, Long idMedico, String observacaoClinica) {
        Consulta c = requererConsulta(idConsulta);
        if (c.getMedico() == null || !c.getMedico().getId().equals(idMedico)) {
            throw new IllegalStateException("Apenas o médico responsável pela consulta pode marcá-la como realizada.");
        }
        c.marcarRealizadaComObservacao(observacaoClinica);
        consultaDAO.atualizar(c);
        registrarHistorico(c, AcaoConsulta.REALIZADA, idMedico, TipoResponsavel.MEDICO,
                c.getObservacaoClinica());
        LOG.info("[ConsultaClinicaServiceImpl] Consulta realizada: consulta=" + idConsulta);

        notificarPaciente(c,
                "Consulta concluída",
                "Sua consulta com Dr(a). " + nomeMedico(c) + " em " + horaFormatada(c)
                        + " foi registrada como realizada.",
                TipoNotificacao.SUCESSO);
    }

    @Override
    public void marcarFaltaPelaClinica(Long idConsulta, Long idResponsavel,
                                        TipoResponsavel tipoResponsavel) {
        if (tipoResponsavel == null) {
            throw new IllegalArgumentException("O tipo do responsável é obrigatório.");
        }
        Consulta c = requererConsulta(idConsulta);
        validarMedicoSoNasProprias(c, idResponsavel, tipoResponsavel);
        c.marcarFalta();
        consultaDAO.atualizar(c);
        registrarHistorico(c, AcaoConsulta.FALTOU, idResponsavel, tipoResponsavel, null);
        LOG.info("[ConsultaClinicaServiceImpl] Falta manual registrada: consulta=" + idConsulta
                + ", responsavel=" + idResponsavel + "/" + tipoResponsavel);

        notificarPaciente(c,
                "Consulta marcada como falta",
                "A consulta com Dr(a). " + nomeMedico(c) + " em " + horaFormatada(c)
                        + " foi marcada como falta pela clínica.",
                TipoNotificacao.ALERTA);
    }

    @Override
    public void cancelarPelaClinica(Long idConsulta, Long idResponsavel,
                                     TipoResponsavel tipoResponsavel, String motivo) {
        if (tipoResponsavel == null) {
            throw new IllegalArgumentException("O tipo do responsável é obrigatório.");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("O motivo do cancelamento é obrigatório.");
        }
        Consulta c = requererConsulta(idConsulta);
        validarMedicoSoNasProprias(c, idResponsavel, tipoResponsavel);
        c.cancelar(motivo);
        consultaDAO.atualizar(c);

        AgendaMedicaSlot slot = c.getSlot();
        if (slot != null) {
            AgendaMedicaSlot slotLock = agendaMedicaSlotDAO.buscarComLock(slot.getId());
            if (slotLock != null) {
                slotLock.liberar();
                agendaMedicaSlotDAO.atualizar(slotLock);
            }
        }
        registrarHistorico(c, AcaoConsulta.CANCELADA, idResponsavel, tipoResponsavel, motivo);
        LOG.info("[ConsultaClinicaServiceImpl] Cancelamento pela clínica: consulta=" + idConsulta
                + ", responsavel=" + idResponsavel + "/" + tipoResponsavel);

        notificarPaciente(c,
                "Consulta cancelada pela clínica",
                "Sua consulta com Dr(a). " + nomeMedico(c) + " em " + horaFormatada(c)
                        + " foi cancelada pela clínica. Motivo: " + motivo,
                TipoNotificacao.ALERTA);
    }

    @Override
    public List<ConsultaHistorico> historicoPorConsulta(Long idConsulta) {
        return historicoDAO.listarPorConsulta(idConsulta);
    }

    @Override
    public ConsultaAnotacao adicionarAnotacao(Long idConsulta, String titulo, String descricao,
                                                Long idResponsavel, TipoResponsavel tipoResponsavel) {
        if (tipoResponsavel == null) {
            throw new IllegalArgumentException("O tipo do responsável é obrigatório.");
        }
        if (tipoResponsavel != TipoResponsavel.MEDICO
                && tipoResponsavel != TipoResponsavel.ENFERMEIRO
                && tipoResponsavel != TipoResponsavel.ADMIN) {
            throw new IllegalStateException("Apenas médicos, enfermeiros ou administradores podem adicionar anotações.");
        }
        Consulta c = requererConsulta(idConsulta);
        if (c.getStatus() == StatusConsulta.CANCELADA) {
            throw new IllegalStateException("Não é possível adicionar anotações em consultas canceladas.");
        }
        if (tipoResponsavel == TipoResponsavel.MEDICO) {
            if (c.getMedico() == null || !c.getMedico().getId().equals(idResponsavel)) {
                throw new IllegalStateException("Médicos só podem anotar nas próprias consultas.");
            }
        }
        ConsultaAnotacao a = ConsultaAnotacao.registrar(c, titulo, descricao, idResponsavel, tipoResponsavel);
        ConsultaAnotacao salva = anotacaoDAO.salvar(a);
        LOG.info("[ConsultaClinicaServiceImpl] Anotação registrada: consulta=" + idConsulta
                + ", responsavel=" + idResponsavel + "/" + tipoResponsavel);
        return salva;
    }

    @Override
    public List<ConsultaAnotacao> listarAnotacoes(Long idConsulta) {
        return anotacaoDAO.listarPorConsulta(idConsulta);
    }

    private void validarMedicoSoNasProprias(Consulta c, Long idResponsavel,
                                              TipoResponsavel tipoResponsavel) {
        if (tipoResponsavel == TipoResponsavel.MEDICO) {
            if (c.getMedico() == null || !c.getMedico().getId().equals(idResponsavel)) {
                throw new IllegalStateException(
                        "Médicos só podem atuar nas próprias consultas.");
            }
        }
    }

    private void registrarHistorico(Consulta c, AcaoConsulta acao, Long idResponsavel,
                                      TipoResponsavel tipoResponsavel, String observacao) {
        try {
            historicoDAO.salvar(ConsultaHistorico.registrar(
                    c, acao, idResponsavel, tipoResponsavel, observacao));
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[ConsultaClinicaServiceImpl] Falha ao registrar histórico", ex);
        }
    }

    private Consulta requererConsulta(Long id) {
        Consulta c = consultaDAO.buscarPorIdComMedico(id);
        if (c == null) {
            throw new IllegalArgumentException("Consulta não encontrada.");
        }
        return c;
    }

    private void notificarMedico(Consulta c, String titulo, String msg, TipoNotificacao tipo) {
        if (emissor == null || c.getMedico() == null) return;
        try {
            emissor.emitir(TipoDestinatarioNotificacao.MEDICO, c.getMedico().getId(),
                    titulo, msg, tipo, CategoriaNotificacao.CONSULTA, "/clinica/minha-agenda.xhtml");
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[ConsultaClinicaServiceImpl] Falha notif médico", ex);
        }
    }

    private void notificarPaciente(Consulta c, String titulo, String msg, TipoNotificacao tipo) {
        if (emissor == null || c.getPaciente() == null) return;
        try {
            emissor.emitir(TipoDestinatarioNotificacao.PACIENTE, c.getPaciente().getId(),
                    titulo, msg, tipo, CategoriaNotificacao.CONSULTA, "/paciente/minhas-consultas.xhtml");
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[ConsultaClinicaServiceImpl] Falha notif paciente", ex);
        }
    }

    private String nomePaciente(Consulta c) {
        return c.getPaciente() != null ? c.getPaciente().getNomeCompleto() : "—";
    }

    private String nomeMedico(Consulta c) {
        return c.getMedico() != null ? c.getMedico().getNomeCompleto() : "—";
    }

    private String horaFormatada(Consulta c) {
        return c.getDataConsulta() != null ? c.getDataConsulta().format(FMT) : "—";
    }
}
