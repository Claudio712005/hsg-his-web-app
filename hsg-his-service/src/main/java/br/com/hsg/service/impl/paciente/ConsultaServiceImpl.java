package br.com.hsg.service.impl.paciente;

import br.com.hsg.dao.AgendaMedicaSlotDAO;
import br.com.hsg.dao.ConsultaDAO;
import br.com.hsg.dao.PacienteConvenioDAO;
import br.com.hsg.dao.PacienteDAO;
import br.com.hsg.dao.RegraCoberturaDAO;
import br.com.hsg.domain.entity.AgendaMedicaSlot;
import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.entity.Especialidade;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.domain.entity.PacienteConvenio;
import br.com.hsg.domain.entity.RegraCobertura;
import br.com.hsg.domain.enums.StatusSlotAgenda;
import br.com.hsg.domain.enums.TipoAtendimentoConsulta;
import br.com.hsg.domain.enums.CategoriaNotificacao;
import br.com.hsg.domain.enums.TipoDestinatarioNotificacao;
import br.com.hsg.domain.enums.TipoNotificacao;
import br.com.hsg.service.dto.ResultadoFinanceiroConsulta;
import br.com.hsg.service.facade.notificacao.NotificacaoServiceFacade;
import br.com.hsg.service.facade.paciente.ConsultaServiceFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class ConsultaServiceImpl implements ConsultaServiceFacade {

    private static final Logger LOG = Logger.getLogger(ConsultaServiceImpl.class.getName());

    private static final int ANTECEDENCIA_MIN_HORAS = 2;
    private static final int ANTECEDENCIA_MAX_DIAS  = 90;
    private static final int LIMITE_FUTURAS         = 3;
    private static final BigDecimal VALOR_PADRAO     = new BigDecimal("250.00");

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    private static final java.time.format.DateTimeFormatter FMT_NOTIF =
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @EJB private ConsultaDAO          consultaDAO;
    @EJB private br.com.hsg.dao.ConsultaHistoricoDAO consultaHistoricoDAO;
    @EJB private AgendaMedicaSlotDAO  agendaMedicaSlotDAO;
    @EJB private PacienteDAO          pacienteDAO;
    @EJB private PacienteConvenioDAO  pacienteConvenioDAO;
    @EJB private RegraCoberturaDAO    regraCoberturaDAO;
    @EJB private NotificacaoServiceFacade notificacaoService;

    @Override
    public ResultadoFinanceiroConsulta simular(Long idPaciente, Long idSlot, boolean usarConvenio) {
        AgendaMedicaSlot slot = agendaMedicaSlotDAO.buscarPorId(idSlot);
        if (slot == null) {
            throw new IllegalArgumentException("Horário não encontrado.");
        }
        BigDecimal valorBase = valorBase(slot.getMedico() != null ? slot.getMedico().getValorConsulta() : null);
        return montarFinanceiro(idPaciente, valorBase, usarConvenio, false).resultado;
    }

    @Override
    public Consulta agendar(Long idPaciente, Long idEspecialidade, Long idSlot, boolean usarConvenio) {
        Paciente paciente = pacienteDAO.buscarPorId(idPaciente);
        if (paciente == null) {
            throw new IllegalArgumentException("Paciente não encontrado.");
        }

        AgendaMedicaSlot slot = agendaMedicaSlotDAO.buscarComLock(idSlot);
        if (slot == null) {
            throw new IllegalArgumentException("Horário não encontrado.");
        }
        if (slot.getStatus() != StatusSlotAgenda.LIVRE) {
            throw new IllegalStateException("Este horário não está mais disponível. Escolha outro.");
        }

        LocalDateTime agora = LocalDateTime.now();
        if (slot.getDataInicio().isBefore(agora.plusHours(ANTECEDENCIA_MIN_HORAS))) {
            throw new IllegalStateException("O agendamento exige no mínimo "
                    + ANTECEDENCIA_MIN_HORAS + "h de antecedência.");
        }
        if (slot.getDataInicio().isAfter(agora.plusDays(ANTECEDENCIA_MAX_DIAS))) {
            throw new IllegalStateException("Só é possível agendar com até "
                    + ANTECEDENCIA_MAX_DIAS + " dias de antecedência.");
        }

        long futuras = consultaDAO.contarFuturasAtivasPorPaciente(idPaciente, agora);
        if (futuras >= LIMITE_FUTURAS) {
            throw new IllegalStateException("Limite de " + LIMITE_FUTURAS
                    + " consultas futuras atingido. Cancele uma antes de agendar outra.");
        }

        Especialidade especialidade = idEspecialidade != null
                ? em.find(Especialidade.class, idEspecialidade) : null;

        BigDecimal valorBase = valorBase(slot.getMedico() != null ? slot.getMedico().getValorConsulta() : null);
        Financeiro fin = montarFinanceiro(idPaciente, valorBase, usarConvenio, true);

        Consulta consulta = Consulta.criar(paciente, slot.getMedico(), especialidade, slot,
                fin.pacienteConvenio, fin.resultado.getTipoAtendimento(),
                fin.resultado.getValorConsulta(), fin.resultado.getValorCopagamento(),
                fin.resultado.getValorCoberturaConvenio());

        Consulta salva = consultaDAO.salvar(consulta);
        slot.reservar(salva.getId());
        agendaMedicaSlotDAO.atualizar(slot);

        LOG.info("[ConsultaServiceImpl] Consulta agendada: id=" + salva.getId()
                + ", paciente=" + idPaciente + ", slot=" + idSlot
                + ", tipo=" + fin.resultado.getTipoAtendimento());

        registrarHistoricoSeguro(salva,
                br.com.hsg.domain.enums.AcaoConsulta.AGENDADA,
                idPaciente,
                br.com.hsg.domain.enums.TipoResponsavel.PACIENTE, null);

        notificarAgendamento(salva);
        return salva;
    }

    private void registrarHistoricoSeguro(Consulta c, br.com.hsg.domain.enums.AcaoConsulta acao,
                                            Long idResponsavel,
                                            br.com.hsg.domain.enums.TipoResponsavel tipoResp,
                                            String observacao) {
        if (consultaHistoricoDAO == null) return;
        try {
            consultaHistoricoDAO.salvar(br.com.hsg.domain.entity.ConsultaHistorico.registrar(
                    c, acao, idResponsavel, tipoResp, observacao));
        } catch (Exception ex) {
            LOG.log(java.util.logging.Level.WARNING,
                    "[ConsultaServiceImpl] Falha ao registrar histórico", ex);
        }
    }

    private void notificarAgendamento(Consulta c) {
        String quando = c.getDataConsulta().format(FMT_NOTIF);
        String medicoNome = c.getMedico() != null ? c.getMedico().getNomeCompleto() : "—";
        String pacienteNome = c.getPaciente() != null ? c.getPaciente().getNomeCompleto() : "—";

        notificarSeguro(TipoDestinatarioNotificacao.PACIENTE, c.getPaciente().getId(),
                "Consulta agendada",
                "Sua consulta com Dr(a). " + medicoNome + " foi marcada para " + quando + ".",
                TipoNotificacao.SUCESSO, CategoriaNotificacao.CONSULTA,
                "/paciente/minhas-consultas.xhtml");

        if (c.getMedico() != null) {
            notificarSeguro(TipoDestinatarioNotificacao.MEDICO, c.getMedico().getId(),
                    "Nova consulta agendada",
                    "Paciente " + pacienteNome + " marcou consulta para " + quando + ".",
                    TipoNotificacao.INFO, CategoriaNotificacao.CONSULTA,
                    "/clinica/notificacoes.xhtml");
        }
    }

    private void notificarSeguro(TipoDestinatarioNotificacao td, Long idDest,
                                  String titulo, String msg,
                                  TipoNotificacao tipo, CategoriaNotificacao cat, String link) {
        if (idDest == null) return;
        try {
            notificacaoService.notificar(td, idDest, titulo, msg, tipo, cat, link);
        } catch (Exception ex) {
            LOG.log(java.util.logging.Level.WARNING,
                    "[ConsultaServiceImpl] Falha ao gerar notificação", ex);
        }
    }

    @Override
    public void cancelarPeloPaciente(Long idConsulta, Long idPaciente, String motivo) {
        Consulta consulta = consultaDAO.buscarPorId(idConsulta);
        if (consulta == null) {
            throw new IllegalArgumentException("Consulta não encontrada.");
        }
        if (consulta.getPaciente() == null || !idPaciente.equals(consulta.getPaciente().getId())) {
            throw new IllegalStateException("Esta consulta não pertence ao paciente.");
        }
        if (consulta.getDataConsulta().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new IllegalStateException("Cancelamento permitido apenas com 24h ou mais de antecedência.");
        }

        consulta.cancelar(motivo);
        consultaDAO.atualizar(consulta);

        AgendaMedicaSlot slot = consulta.getSlot();
        if (slot != null) {
            AgendaMedicaSlot slotLock = agendaMedicaSlotDAO.buscarComLock(slot.getId());
            if (slotLock != null) {
                slotLock.liberar();
                agendaMedicaSlotDAO.atualizar(slotLock);
            }
        }

        LOG.info("[ConsultaServiceImpl] Consulta cancelada: id=" + idConsulta + ", paciente=" + idPaciente);

        registrarHistoricoSeguro(consulta,
                br.com.hsg.domain.enums.AcaoConsulta.CANCELADA,
                idPaciente,
                br.com.hsg.domain.enums.TipoResponsavel.PACIENTE, motivo);

        if (consulta.getMedico() != null) {
            String quando = consulta.getDataConsulta().format(FMT_NOTIF);
            String pacienteNome = consulta.getPaciente() != null
                    ? consulta.getPaciente().getNomeCompleto() : "—";
            notificarSeguro(TipoDestinatarioNotificacao.MEDICO, consulta.getMedico().getId(),
                    "Consulta cancelada pelo paciente",
                    "Paciente " + pacienteNome + " cancelou a consulta de " + quando
                            + ". Motivo: " + motivo,
                    TipoNotificacao.ALERTA, CategoriaNotificacao.CONSULTA,
                    "/clinica/notificacoes.xhtml");
        }
    }

    @Override
    public List<Consulta> listarConsultasPaciente(Long idPaciente) {
        return consultaDAO.listarPorPaciente(idPaciente);
    }

    @Override
    public List<Consulta> listarProximasPaciente(Long idPaciente, int limite) {
        if (idPaciente == null) return java.util.Collections.emptyList();
        int l = (limite <= 0 || limite > 50) ? 5 : limite;
        return consultaDAO.listarProximasPorPaciente(idPaciente, LocalDateTime.now(), l);
    }

    @Override
    public List<Consulta> listarProximasMedico(Long idMedico, int limite) {
        if (idMedico == null) return java.util.Collections.emptyList();
        int l = (limite <= 0 || limite > 50) ? 5 : limite;
        return consultaDAO.listarProximasPorMedico(idMedico, LocalDateTime.now(), l);
    }

    private BigDecimal valorBase(BigDecimal valorMedico) {
        return valorMedico != null ? valorMedico : VALOR_PADRAO;
    }

    private Financeiro montarFinanceiro(Long idPaciente, BigDecimal valorBase,
                                        boolean usarConvenio, boolean strict) {
        if (!usarConvenio) {
            return new Financeiro(particular(valorBase), null);
        }

        PacienteConvenio convenio = pacienteConvenioDAO.buscarAtivoPorPaciente(idPaciente);
        if (convenio == null) {
            if (strict) {
                throw new IllegalStateException("Paciente não possui convênio ativo. Agende como particular.");
            }
            return new Financeiro(particular(valorBase), null);
        }

        RegraCobertura regra = buscarRegraConsulta(convenio.getPlano().getId());
        if (regra == null || !regra.isCoberto()) {
            if (strict) {
                throw new IllegalStateException("O plano não cobre consultas. Agende como particular.");
            }
            return new Financeiro(new ResultadoFinanceiroConsulta(
                    TipoAtendimentoConsulta.PARTICULAR, valorBase, valorBase, BigDecimal.ZERO,
                    false, false, "Plano não cobre consultas — atendimento particular."), null);
        }

        boolean emCarencia = estaEmCarencia(convenio, regra);
        if (emCarencia) {
            if (strict) {
                throw new IllegalStateException("Procedimento em carência pelo convênio. Agende como particular.");
            }
            return new Financeiro(new ResultadoFinanceiroConsulta(
                    TipoAtendimentoConsulta.PARTICULAR, valorBase, valorBase, BigDecimal.ZERO,
                    false, true, "Convênio em carência — atendimento particular."), null);
        }

        BigDecimal pct = regra.getPercentualCopagamento() != null
                ? regra.getPercentualCopagamento() : BigDecimal.ZERO;
        BigDecimal copag = valorBase.multiply(pct)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal cobertura = valorBase.subtract(copag).setScale(2, RoundingMode.HALF_UP);

        ResultadoFinanceiroConsulta r = new ResultadoFinanceiroConsulta(
                TipoAtendimentoConsulta.CONVENIO, valorBase, copag, cobertura,
                true, false, "Atendimento pelo convênio.");
        return new Financeiro(r, convenio);
    }

    private ResultadoFinanceiroConsulta particular(BigDecimal valorBase) {
        return new ResultadoFinanceiroConsulta(
                TipoAtendimentoConsulta.PARTICULAR, valorBase, valorBase, BigDecimal.ZERO,
                false, false, "Atendimento particular.");
    }

    private RegraCobertura buscarRegraConsulta(Long idPlano) {
        List<RegraCobertura> regras = regraCoberturaDAO.listarAtivasPorPlano(idPlano);
        RegraCobertura fallback = null;
        for (RegraCobertura r : regras) {
            boolean ehConsulta = (r.getCategoria() != null && r.getCategoria().equalsIgnoreCase("Consultas"))
                    || (r.getProcedimento() != null && r.getProcedimento().toLowerCase().contains("consulta"));
            if (!ehConsulta) continue;
            if (r.isCoberto()) return r;
            if (fallback == null) fallback = r;
        }
        return fallback;
    }

    private boolean estaEmCarencia(PacienteConvenio convenio, RegraCobertura regra) {
        if (convenio.getDataAdesao() == null) return false;
        int dias = regra.getCarenciaDias() != null ? regra.getCarenciaDias() : 0;
        LocalDate liberacao = convenio.getDataAdesao().toLocalDate().plusDays(dias);
        return LocalDate.now().isBefore(liberacao);
    }

    private static final class Financeiro {
        final ResultadoFinanceiroConsulta resultado;
        final PacienteConvenio pacienteConvenio;
        Financeiro(ResultadoFinanceiroConsulta resultado, PacienteConvenio pacienteConvenio) {
            this.resultado = resultado;
            this.pacienteConvenio = pacienteConvenio;
        }
    }
}
