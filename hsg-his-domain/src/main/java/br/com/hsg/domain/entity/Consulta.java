package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.StatusConsulta;
import br.com.hsg.domain.enums.TipoAtendimentoConsulta;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_CONSULTA", schema = "hsg",
        uniqueConstraints = @UniqueConstraint(columnNames = "ID_AGENDA_SLOT"))
public class Consulta {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CONSULTA")
    @SequenceGenerator(name = "SEQ_CONSULTA", sequenceName = "SEQ_CONSULTA", allocationSize = 1)
    @Column(name = "ID_CONSULTA")
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PACIENTE", nullable = false)
    private Paciente paciente;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MEDICO", nullable = false)
    private Medico medico;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ESPECIALIDADE")
    private Especialidade especialidade;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AGENDA_SLOT", nullable = false)
    private AgendaMedicaSlot slot;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PACIENTE_CONVENIO")
    private PacienteConvenio pacienteConvenio;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_ATENDIMENTO", length = 12, nullable = false)
    private TipoAtendimentoConsulta tipoAtendimento;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "ST_CONSULTA", length = 15, nullable = false)
    private StatusConsulta status;

    @Getter
    @Column(name = "DT_CONSULTA", nullable = false)
    private LocalDateTime dataConsulta;

    @Getter
    @Column(name = "DT_CANCELAMENTO")
    private LocalDateTime dataCancelamento;

    @Getter
    @Column(name = "DS_CANCELAMENTO", length = 500)
    private String motivoCancelamento;

    @Getter
    @Column(name = "VL_CONSULTA", precision = 10, scale = 2)
    private BigDecimal valorConsulta;

    @Getter
    @Column(name = "VL_COPAGAMENTO", precision = 10, scale = 2)
    private BigDecimal valorCopagamento;

    @Getter
    @Column(name = "VL_COBERTURA_CONVENIO", precision = 10, scale = 2)
    private BigDecimal valorCoberturaConvenio;

    @Getter
    @Column(name = "DT_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;

    @Getter
    @Column(name = "DT_ULT_ATU")
    private LocalDateTime dataUltimaAtualizacao;

    protected Consulta() {}

    public static Consulta criar(Paciente paciente, Medico medico, Especialidade especialidade,
                                  AgendaMedicaSlot slot, PacienteConvenio pacienteConvenio,
                                  TipoAtendimentoConsulta tipoAtendimento,
                                  BigDecimal valorConsulta, BigDecimal valorCopagamento,
                                  BigDecimal valorCoberturaConvenio) {
        validar(paciente, medico, slot, tipoAtendimento, pacienteConvenio);
        Consulta c = new Consulta();
        c.paciente               = paciente;
        c.medico                 = medico;
        c.especialidade          = especialidade;
        c.slot                   = slot;
        c.pacienteConvenio       = pacienteConvenio;
        c.tipoAtendimento        = tipoAtendimento;
        c.status                 = StatusConsulta.AGENDADA;
        c.dataConsulta           = slot.getDataInicio();
        c.valorConsulta          = valorConsulta;
        c.valorCopagamento       = valorCopagamento;
        c.valorCoberturaConvenio = valorCoberturaConvenio;
        c.dataCadastro           = LocalDateTime.now();
        return c;
    }

    public static void validar(Paciente paciente, Medico medico, AgendaMedicaSlot slot,
                                TipoAtendimentoConsulta tipoAtendimento, PacienteConvenio pacienteConvenio) {
        if (paciente == null) {
            throw new IllegalArgumentException("O paciente é obrigatório.");
        }
        if (medico == null) {
            throw new IllegalArgumentException("O médico é obrigatório.");
        }
        if (slot == null) {
            throw new IllegalArgumentException("O horário (slot) é obrigatório.");
        }
        if (tipoAtendimento == null) {
            throw new IllegalArgumentException("O tipo de atendimento é obrigatório.");
        }
        if (TipoAtendimentoConsulta.CONVENIO.equals(tipoAtendimento) && pacienteConvenio == null) {
            throw new IllegalArgumentException("Atendimento por convênio exige vínculo de paciente-convênio ativo.");
        }
    }

    public void confirmar() {
        if (this.status != StatusConsulta.AGENDADA) {
            throw new IllegalStateException("Apenas consultas agendadas podem ser confirmadas.");
        }
        this.status                = StatusConsulta.CONFIRMADA;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public void marcarRealizada() {
        if (this.status != StatusConsulta.CONFIRMADA && this.status != StatusConsulta.AGENDADA) {
            throw new IllegalStateException("Apenas consultas agendadas ou confirmadas podem ser marcadas como realizadas.");
        }
        this.status                = StatusConsulta.REALIZADA;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public void marcarFalta() {
        if (this.status != StatusConsulta.CONFIRMADA && this.status != StatusConsulta.AGENDADA) {
            throw new IllegalStateException("Apenas consultas agendadas ou confirmadas podem ser marcadas como falta.");
        }
        this.status                = StatusConsulta.FALTOU;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public void cancelar(String motivo) {
        if (this.status == StatusConsulta.REALIZADA || this.status == StatusConsulta.CANCELADA) {
            throw new IllegalStateException("Consulta já finalizada não pode ser cancelada.");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("O motivo do cancelamento é obrigatório.");
        }
        this.status                = StatusConsulta.CANCELADA;
        this.motivoCancelamento    = motivo.trim();
        this.dataCancelamento      = LocalDateTime.now();
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public boolean isAgendada()   { return StatusConsulta.AGENDADA.equals(status); }
    public boolean isConfirmada() { return StatusConsulta.CONFIRMADA.equals(status); }
    public boolean isCancelada()  { return StatusConsulta.CANCELADA.equals(status); }
}
