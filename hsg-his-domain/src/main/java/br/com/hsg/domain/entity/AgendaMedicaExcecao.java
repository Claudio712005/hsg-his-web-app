package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.TipoExcecaoAgenda;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_AGENDA_MEDICA_EXCECAO", schema = "hsg")
public class AgendaMedicaExcecao {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_AGENDA_MEDICA_EXCECAO")
    @SequenceGenerator(name = "SEQ_AGENDA_MEDICA_EXCECAO",
            sequenceName = "SEQ_AGENDA_MEDICA_EXCECAO", allocationSize = 1)
    @Column(name = "ID_AGENDA_EXCECAO")
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MEDICO", nullable = false)
    private Medico medico;

    @Getter
    @Column(name = "DT_INICIO", nullable = false)
    private LocalDateTime dataInicio;

    @Getter
    @Column(name = "DT_FIM", nullable = false)
    private LocalDateTime dataFim;

    @Getter
    @Column(name = "DS_MOTIVO", length = 300)
    private String motivo;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_EXCECAO", length = 20, nullable = false)
    private TipoExcecaoAgenda tipo;

    @Getter
    @Column(name = "DT_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;

    protected AgendaMedicaExcecao() {}

    public static AgendaMedicaExcecao criar(Medico medico, LocalDateTime dataInicio,
                                             LocalDateTime dataFim, TipoExcecaoAgenda tipo, String motivo) {
        if (medico == null) {
            throw new IllegalArgumentException("O médico é obrigatório.");
        }
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("As datas de início e fim são obrigatórias.");
        }
        if (!dataInicio.isBefore(dataFim)) {
            throw new IllegalArgumentException("A data de início deve ser anterior à data de fim.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo da exceção é obrigatório.");
        }
        AgendaMedicaExcecao e = new AgendaMedicaExcecao();
        e.medico       = medico;
        e.dataInicio   = dataInicio;
        e.dataFim      = dataFim;
        e.tipo         = tipo;
        e.motivo       = motivo;
        e.dataCadastro = LocalDateTime.now();
        return e;
    }
}
