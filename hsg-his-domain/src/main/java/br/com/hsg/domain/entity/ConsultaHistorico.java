package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.AcaoConsulta;
import br.com.hsg.domain.enums.TipoResponsavel;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_CONSULTA_HISTORICO", schema = "hsg")
public class ConsultaHistorico {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CONSULTA_HISTORICO")
    @SequenceGenerator(name = "SEQ_CONSULTA_HISTORICO",
            sequenceName = "SEQ_CONSULTA_HISTORICO", allocationSize = 1)
    @Column(name = "ID_CONSULTA_HISTORICO")
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONSULTA", nullable = false)
    private Consulta consulta;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_ACAO", length = 15, nullable = false)
    private AcaoConsulta acao;

    @Getter
    @Column(name = "ID_RESPONSAVEL")
    private Long idResponsavel;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_RESPONSAVEL", length = 12, nullable = false)
    private TipoResponsavel tipoResponsavel;

    @Getter
    @Column(name = "DS_OBSERVACAO", length = 1000)
    private String observacao;

    @Getter
    @Column(name = "DT_ACAO", nullable = false)
    private LocalDateTime dataAcao;

    protected ConsultaHistorico() {}

    public static ConsultaHistorico registrar(Consulta consulta, AcaoConsulta acao,
                                                Long idResponsavel,
                                                TipoResponsavel tipoResponsavel,
                                                String observacao) {
        if (consulta == null) {
            throw new IllegalArgumentException("A consulta é obrigatória.");
        }
        if (acao == null) {
            throw new IllegalArgumentException("A ação é obrigatória.");
        }
        if (tipoResponsavel == null) {
            throw new IllegalArgumentException("O tipo do responsável é obrigatório.");
        }
        ConsultaHistorico h = new ConsultaHistorico();
        h.consulta         = consulta;
        h.acao             = acao;
        h.idResponsavel    = idResponsavel;
        h.tipoResponsavel  = tipoResponsavel;
        h.observacao       = observacao;
        h.dataAcao         = LocalDateTime.now();
        return h;
    }
}
