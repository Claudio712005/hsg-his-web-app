package br.com.hsg.service.impl.admin;

import br.com.hsg.dao.AgendaMedicaDAO;
import br.com.hsg.dao.AgendaMedicaExcecaoDAO;
import br.com.hsg.dao.AgendaMedicaSlotDAO;
import br.com.hsg.dao.MedicoDAO;
import br.com.hsg.dao.MedicoEspecialidadeDAO;
import br.com.hsg.domain.entity.AgendaMedica;
import br.com.hsg.domain.entity.AgendaMedicaExcecao;
import br.com.hsg.domain.entity.AgendaMedicaSlot;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.entity.MedicoEspecialidade;
import br.com.hsg.domain.enums.DiaSemana;
import br.com.hsg.domain.enums.StatusSlotAgenda;
import br.com.hsg.domain.enums.TipoExcecaoAgenda;
import br.com.hsg.service.facade.admin.AgendaMedicaServiceFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class AgendaMedicaServiceImpl implements AgendaMedicaServiceFacade {

    private static final Logger LOG = Logger.getLogger(AgendaMedicaServiceImpl.class.getName());

    @EJB private MedicoDAO                medicoDAO;
    @EJB private MedicoEspecialidadeDAO   medicoEspecialidadeDAO;
    @EJB private AgendaMedicaDAO          agendaMedicaDAO;
    @EJB private AgendaMedicaExcecaoDAO   agendaMedicaExcecaoDAO;
    @EJB private AgendaMedicaSlotDAO      agendaMedicaSlotDAO;

    @Override
    public List<Medico> listarMedicosAtivos() {
        return medicoDAO.listarAtivos();
    }

    @Override
    public List<MedicoEspecialidade> listarEspecialidadesDoMedico(Long idMedico) {
        return medicoEspecialidadeDAO.listarPorMedico(idMedico);
    }

    @Override
    public List<AgendaMedica> listarGradesPorMedico(Long idMedico) {
        return agendaMedicaDAO.listarPorMedico(idMedico);
    }

    @Override
    public AgendaMedica criarGrade(Long idMedico, DiaSemana diaSemana, LocalTime horaInicio,
                                    LocalTime horaFim, Integer duracaoMinutos) {
        Medico medico = medicoDAO.buscarPorId(idMedico);
        if (medico == null) {
            throw new IllegalArgumentException("Médico não encontrado.");
        }
        validarSobreposicao(idMedico, diaSemana, horaInicio, horaFim, null);
        AgendaMedica a = AgendaMedica.criar(medico, diaSemana, horaInicio, horaFim, duracaoMinutos);
        AgendaMedica salva = agendaMedicaDAO.salvar(a);
        LOG.info("[AgendaMedicaServiceImpl] Grade criada: id=" + salva.getId()
                + ", medico=" + idMedico + ", dia=" + diaSemana);
        return salva;
    }

    @Override
    public AgendaMedica atualizarGrade(Long idGrade, LocalTime horaInicio, LocalTime horaFim,
                                        Integer duracaoMinutos) {
        AgendaMedica a = agendaMedicaDAO.buscarPorId(idGrade);
        if (a == null) {
            throw new IllegalArgumentException("Grade não encontrada.");
        }
        validarSobreposicao(a.getMedico().getId(), a.getDiaSemana(), horaInicio, horaFim, idGrade);
        a.atualizar(horaInicio, horaFim, duracaoMinutos);
        return agendaMedicaDAO.atualizar(a);
    }

    @Override
    public void ativarGrade(Long idGrade) {
        AgendaMedica a = agendaMedicaDAO.buscarPorId(idGrade);
        if (a == null) {
            throw new IllegalArgumentException("Grade não encontrada.");
        }
        validarSobreposicao(a.getMedico().getId(), a.getDiaSemana(),
                a.getHoraInicio(), a.getHoraFim(), idGrade);
        a.ativar();
        agendaMedicaDAO.atualizar(a);
    }

    @Override
    public void inativarGrade(Long idGrade) {
        AgendaMedica a = agendaMedicaDAO.buscarPorId(idGrade);
        if (a == null) {
            throw new IllegalArgumentException("Grade não encontrada.");
        }
        a.inativar();
        agendaMedicaDAO.atualizar(a);
    }

    private void validarSobreposicao(Long idMedico, DiaSemana dia,
                                      LocalTime horaInicio, LocalTime horaFim, Long idExcluir) {
        List<AgendaMedica> conflitos = agendaMedicaDAO.buscarSobreposicao(
                idMedico, dia, horaInicio, horaFim, idExcluir);
        if (!conflitos.isEmpty()) {
            AgendaMedica c = conflitos.get(0);
            throw new IllegalStateException("Já existe grade ativa neste dia/horário ("
                    + c.getHoraInicio() + " — " + c.getHoraFim() + ").");
        }
    }

    @Override
    public List<AgendaMedicaExcecao> listarExcecoesPorMedico(Long idMedico) {
        return agendaMedicaExcecaoDAO.listarPorMedico(idMedico);
    }

    @Override
    public AgendaMedicaExcecao criarExcecao(Long idMedico, LocalDateTime inicio, LocalDateTime fim,
                                             TipoExcecaoAgenda tipo, String motivo) {
        Medico medico = medicoDAO.buscarPorId(idMedico);
        if (medico == null) {
            throw new IllegalArgumentException("Médico não encontrado.");
        }
        validarSobreposicaoExcecao(idMedico, inicio, fim, null);
        AgendaMedicaExcecao e = AgendaMedicaExcecao.criar(medico, inicio, fim, tipo, motivo);
        AgendaMedicaExcecao salva = agendaMedicaExcecaoDAO.salvar(e);
        LOG.info("[AgendaMedicaServiceImpl] Exceção criada: id=" + salva.getId()
                + ", medico=" + idMedico + ", tipo=" + tipo);
        return salva;
    }

    private void validarSobreposicaoExcecao(Long idMedico, LocalDateTime inicio,
                                             LocalDateTime fim, Long idExcluir) {
        if (inicio == null || fim == null) return;
        List<AgendaMedicaExcecao> conflitos = agendaMedicaExcecaoDAO.buscarSobreposicao(
                idMedico, inicio, fim, idExcluir);
        if (!conflitos.isEmpty()) {
            AgendaMedicaExcecao c = conflitos.get(0);
            throw new IllegalStateException("Já existe exceção " + c.getTipo().getDescricao()
                    + " sobreposta no período (" + c.getDataInicio() + " — " + c.getDataFim() + ").");
        }
    }

    @Override
    public void removerExcecao(Long idExcecao) {
        agendaMedicaExcecaoDAO.remover(idExcecao);
    }

    @Override
    public List<AgendaMedicaSlot> listarSlotsPorMedicoPeriodo(Long idMedico,
                                                               LocalDateTime inicio,
                                                               LocalDateTime fim) {
        return agendaMedicaSlotDAO.listarPorMedicoPeriodo(idMedico, inicio, fim);
    }

    @Override
    public List<AgendaMedicaSlot> listarSlotsPorMedicosPeriodo(List<Long> idsMedicos,
                                                                LocalDateTime inicio,
                                                                LocalDateTime fim) {
        return agendaMedicaSlotDAO.listarPorMedicosPeriodo(idsMedicos, inicio, fim);
    }

    @Override
    public List<AgendaMedicaExcecao> listarExcecoesPorMedicosVigentes(List<Long> idsMedicos,
                                                                       LocalDateTime inicio,
                                                                       LocalDateTime fim) {
        return agendaMedicaExcecaoDAO.listarPorMedicosVigentes(idsMedicos, inicio, fim);
    }

    @Override
    public long contarSlotsLivresFuturos(Long idMedico) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime futuro = agora.plusDays(365);
        return agendaMedicaSlotDAO.contarPorMedicoStatusPeriodo(idMedico, StatusSlotAgenda.LIVRE,
                agora, futuro);
    }

    @Override
    public int gerarSlots(Long idMedico, int diasFrente) {
        Medico medico = medicoDAO.buscarPorId(idMedico);
        if (medico == null) {
            throw new IllegalArgumentException("Médico não encontrado.");
        }
        if (diasFrente <= 0 || diasFrente > 180) {
            throw new IllegalArgumentException("Informe um período entre 1 e 180 dias.");
        }

        List<AgendaMedica> grades = agendaMedicaDAO.listarAtivasPorMedico(idMedico);
        if (grades.isEmpty()) {
            throw new IllegalStateException("Médico não possui grade ativa configurada.");
        }

        LocalDate hoje = LocalDate.now();
        LocalDate fim  = hoje.plusDays(diasFrente);
        LocalDateTime inicioPeriodo = hoje.atStartOfDay();
        LocalDateTime fimPeriodo    = fim.atStartOfDay();

        List<AgendaMedicaExcecao> excecoes = agendaMedicaExcecaoDAO.listarVigentesNoPeriodo(
                idMedico, inicioPeriodo, fimPeriodo);

        int criados = 0;
        for (LocalDate d = hoje; d.isBefore(fim); d = d.plusDays(1)) {
            DiaSemana dow = DiaSemana.fromDayOfWeek(d.getDayOfWeek());
            for (AgendaMedica g : grades) {
                if (g.getDiaSemana() != dow) continue;
                LocalTime hora = g.getHoraInicio();
                while (hora.plusMinutes(g.getDuracaoMinutos()).compareTo(g.getHoraFim()) <= 0) {
                    LocalDateTime ini = LocalDateTime.of(d, hora);
                    LocalDateTime fimSlot = ini.plusMinutes(g.getDuracaoMinutos());

                    if (estaEmExcecao(ini, fimSlot, excecoes)) {
                        hora = hora.plusMinutes(g.getDuracaoMinutos());
                        continue;
                    }
                    if (agendaMedicaSlotDAO.existePorMedicoDataInicio(idMedico, ini)) {
                        hora = hora.plusMinutes(g.getDuracaoMinutos());
                        continue;
                    }
                    AgendaMedicaSlot slot = AgendaMedicaSlot.criar(medico, null, ini, fimSlot);
                    agendaMedicaSlotDAO.salvar(slot);
                    criados++;
                    hora = hora.plusMinutes(g.getDuracaoMinutos());
                }
            }
        }
        agendaMedicaSlotDAO.flush();
        LOG.info("[AgendaMedicaServiceImpl] Slots gerados: medico=" + idMedico
                + ", periodo=" + diasFrente + "d, criados=" + criados);
        return criados;
    }

    private boolean estaEmExcecao(LocalDateTime ini, LocalDateTime fim,
                                   List<AgendaMedicaExcecao> excecoes) {
        for (AgendaMedicaExcecao e : excecoes) {
            if (ini.isBefore(e.getDataFim()) && fim.isAfter(e.getDataInicio())) {
                return true;
            }
        }
        return false;
    }
}
