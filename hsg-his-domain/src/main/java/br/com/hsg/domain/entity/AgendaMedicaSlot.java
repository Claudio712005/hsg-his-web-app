package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.StatusSlotAgenda;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_AGENDA_MEDICA_SLOT", schema = "hsg",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ID_MEDICO", "DT_INICIO"}))
public class AgendaMedicaSlot {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_AGENDA_MEDICA_SLOT")
    @SequenceGenerator(name = "SEQ_AGENDA_MEDICA_SLOT",
            sequenceName = "SEQ_AGENDA_MEDICA_SLOT", allocationSize = 1)
    @Column(name = "ID_AGENDA_SLOT")
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MEDICO", nullable = false)
    private Medico medico;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ESPECIALIDADE")
    private Especialidade especialidade;

    @Getter
    @Column(name = "DT_INICIO", nullable = false)
    private LocalDateTime dataInicio;

    @Getter
    @Column(name = "DT_FIM", nullable = false)
    private LocalDateTime dataFim;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "ST_SLOT", length = 12, nullable = false)
    private StatusSlotAgenda status;

    @Getter
    @Column(name = "ID_CONSULTA")
    private Long idConsulta;

    @Getter
    @Column(name = "DT_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;

    @Getter
    @Column(name = "DT_ULT_ATU")
    private LocalDateTime dataUltimaAtualizacao;

    protected AgendaMedicaSlot() {}

    public static AgendaMedicaSlot criar(Medico medico, Especialidade especialidade,
                                          LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (medico == null) {
            throw new IllegalArgumentException("O médico é obrigatório.");
        }
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("As datas de início e fim são obrigatórias.");
        }
        if (!dataInicio.isBefore(dataFim)) {
            throw new IllegalArgumentException("O início do slot deve ser anterior ao fim.");
        }
        AgendaMedicaSlot s = new AgendaMedicaSlot();
        s.medico        = medico;
        s.especialidade = especialidade;
        s.dataInicio    = dataInicio;
        s.dataFim       = dataFim;
        s.status        = StatusSlotAgenda.LIVRE;
        s.dataCadastro  = LocalDateTime.now();
        return s;
    }

    public void reservar(Long idConsulta) {
        if (this.status != StatusSlotAgenda.LIVRE) {
            throw new IllegalStateException("Apenas slots livres podem ser reservados.");
        }
        this.status                = StatusSlotAgenda.RESERVADO;
        this.idConsulta            = idConsulta;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public void liberar() {
        this.status                = StatusSlotAgenda.LIVRE;
        this.idConsulta            = null;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public void bloquear() {
        if (this.status == StatusSlotAgenda.RESERVADO) {
            throw new IllegalStateException("Slot reservado não pode ser bloqueado diretamente; cancele a consulta antes.");
        }
        this.status                = StatusSlotAgenda.BLOQUEADO;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public void cancelar() {
        this.status                = StatusSlotAgenda.CANCELADO;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public boolean isLivre() {
        return StatusSlotAgenda.LIVRE.equals(status);
    }
}
