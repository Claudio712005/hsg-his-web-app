package br.com.hsg.domain.entity;

import br.com.hsg.domain.converter.DiaSemanaConverter;
import br.com.hsg.domain.converter.IndicativoStatusConverter;
import br.com.hsg.domain.enums.DiaSemana;
import br.com.hsg.domain.enums.IndicativoStatus;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "TB_AGENDA_MEDICA", schema = "hsg")
public class AgendaMedica {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_AGENDA_MEDICA")
    @SequenceGenerator(name = "SEQ_AGENDA_MEDICA",
            sequenceName = "SEQ_AGENDA_MEDICA", allocationSize = 1)
    @Column(name = "ID_AGENDA_MEDICA")
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MEDICO", nullable = false)
    private Medico medico;

    @Getter
    @Convert(converter = DiaSemanaConverter.class)
    @Column(name = "NR_DIA_SEMANA", nullable = false)
    private DiaSemana diaSemana;

    @Getter
    @Column(name = "HR_INICIO", nullable = false)
    private LocalTime horaInicio;

    @Getter
    @Column(name = "HR_FIM", nullable = false)
    private LocalTime horaFim;

    @Getter
    @Column(name = "NR_DURACAO_MIN", nullable = false)
    private Integer duracaoMinutos;

    @Getter
    @Convert(converter = IndicativoStatusConverter.class)
    @Column(name = "ST_ATIVO", length = 1, nullable = false)
    private IndicativoStatus status;

    @Getter
    @Column(name = "DT_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;

    @Getter
    @Column(name = "DT_ULT_ATU")
    private LocalDateTime dataUltimaAtualizacao;

    protected AgendaMedica() {}

    public static AgendaMedica criar(Medico medico, DiaSemana diaSemana,
                                      LocalTime horaInicio, LocalTime horaFim, Integer duracaoMinutos) {
        validar(medico, diaSemana, horaInicio, horaFim, duracaoMinutos);
        AgendaMedica a = new AgendaMedica();
        a.medico         = medico;
        a.diaSemana      = diaSemana;
        a.horaInicio     = horaInicio;
        a.horaFim        = horaFim;
        a.duracaoMinutos = duracaoMinutos;
        a.status         = IndicativoStatus.A;
        a.dataCadastro   = LocalDateTime.now();
        return a;
    }

    public static void validar(Medico medico, DiaSemana diaSemana,
                                LocalTime horaInicio, LocalTime horaFim, Integer duracaoMinutos) {
        if (medico == null) {
            throw new IllegalArgumentException("O médico é obrigatório.");
        }
        if (diaSemana == null) {
            throw new IllegalArgumentException("O dia da semana é obrigatório.");
        }
        if (horaInicio == null || horaFim == null) {
            throw new IllegalArgumentException("Os horários de início e fim são obrigatórios.");
        }
        if (!horaInicio.isBefore(horaFim)) {
            throw new IllegalArgumentException("O horário de início deve ser anterior ao horário de fim.");
        }
        if (duracaoMinutos == null || duracaoMinutos <= 0) {
            throw new IllegalArgumentException("A duração do slot deve ser maior que zero.");
        }
        long minutos = java.time.Duration.between(horaInicio, horaFim).toMinutes();
        if (minutos < duracaoMinutos) {
            throw new IllegalArgumentException("A faixa horária é menor que a duração do slot.");
        }
    }

    public void atualizar(LocalTime horaInicio, LocalTime horaFim, Integer duracaoMinutos) {
        validar(this.medico, this.diaSemana, horaInicio, horaFim, duracaoMinutos);
        this.horaInicio            = horaInicio;
        this.horaFim               = horaFim;
        this.duracaoMinutos        = duracaoMinutos;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public void ativar() {
        this.status = IndicativoStatus.A;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public void inativar() {
        this.status = IndicativoStatus.I;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public boolean isAtiva() {
        return IndicativoStatus.A.equals(status);
    }
}
