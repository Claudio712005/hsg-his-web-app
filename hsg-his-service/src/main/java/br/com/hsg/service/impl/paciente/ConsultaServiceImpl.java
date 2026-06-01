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
import br.com.hsg.service.dto.ResultadoFinanceiroConsulta;
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

    @EJB private ConsultaDAO          consultaDAO;
    @EJB private AgendaMedicaSlotDAO  agendaMedicaSlotDAO;
    @EJB private PacienteDAO          pacienteDAO;
    @EJB private PacienteConvenioDAO  pacienteConvenioDAO;
    @EJB private RegraCoberturaDAO    regraCoberturaDAO;

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
        return salva;
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
    }

    @Override
    public List<Consulta> listarConsultasPaciente(Long idPaciente) {
        return consultaDAO.listarPorPaciente(idPaciente);
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
