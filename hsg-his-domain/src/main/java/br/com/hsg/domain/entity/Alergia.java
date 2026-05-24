package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.GravidadeAlergia;
import br.com.hsg.domain.enums.StatusAlergia;
import br.com.hsg.domain.enums.TipoAlergia;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "TB_ALRG", schema = "hsg")
public class Alergia {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ALRG")
    @SequenceGenerator(name = "SEQ_ALRG", sequenceName = "SEQ_ALRG", allocationSize = 1)
    @Column(name = "ID_ALRG")
    private Long id;

    @Getter
    @Column(name = "NM_ALRG", length = 100, nullable = false)
    private String nome;

    @Getter
    @Column(name = "DS_ALRG", length = 500)
    private String descricao;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_ALRG", length = 2, nullable = false)
    private TipoAlergia tipoAlergia;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_GRAV_ALRG", length = 1, nullable = false)
    private GravidadeAlergia gravidadeAlergia;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "ST_ALERGIA", length = 10, nullable = false)
    private StatusAlergia statusAlergia;

    @Getter
    @Column(name = "OBS_ALRG", length = 500)
    private String observacao;

    @Getter
    @Column(name = "OBS_ENF_ALRG", length = 500)
    private String observacaoEnfermeiro;

    @Getter
    @Column(name = "DS_REACAO", length = 500)
    private String reacao;

    @Getter
    @Column(name = "ID_CAD_ALRG")
    private Long idCadastrador;

    @Getter
    @Column(name = "ID_APR_ALRG")
    private Long idAprovador;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PAC", nullable = false)
    private Paciente paciente;

    @Getter
    @Column(name = "DT_ULT_REACAO")
    private LocalDateTime dataUltimaReacao;

    @Getter
    @Column(name = "DT_CAD_ALRG")
    private LocalDateTime dataCadastro;

    @Getter
    @Column(name = "DT_ULT_ATU_ALRG")
    private LocalDateTime dataUltimaAlteracao;

    @Getter
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "alergia")
    private List<AlergiaHistorico>  alergiasHistorico;

    protected Alergia() {}

    public static Alergia informar(
            Paciente paciente,
            Long idCadastrador,
            String nome,
            String descricao,
            TipoAlergia tipoAlergia,
            GravidadeAlergia gravidadeAlergia,
            String reacao,
            LocalDateTime dataUltimaReacao,
            String observacao) {

        Alergia a = new Alergia();
        a.paciente          = paciente;
        a.idCadastrador     = idCadastrador;
        a.nome              = nome;
        a.descricao         = descricao;
        a.tipoAlergia       = tipoAlergia;
        a.gravidadeAlergia  = gravidadeAlergia;
        a.reacao            = reacao;
        a.dataUltimaReacao  = dataUltimaReacao;
        a.observacao        = observacao;
        a.statusAlergia     = StatusAlergia.INFORMADA;
        a.dataCadastro      = LocalDateTime.now();
        a.dataUltimaAlteracao = LocalDateTime.now();
        return a;
    }

    public void atualizar(
            String nome,
            String descricao,
            TipoAlergia tipoAlergia,
            GravidadeAlergia gravidadeAlergia,
            String reacao,
            LocalDateTime dataUltimaReacao,
            String observacao) {

        this.nome             = nome;
        this.descricao        = descricao;
        this.tipoAlergia      = tipoAlergia;
        this.gravidadeAlergia = gravidadeAlergia;
        this.reacao           = reacao;
        this.dataUltimaReacao = dataUltimaReacao;
        this.observacao       = observacao;
        this.statusAlergia    = StatusAlergia.INFORMADA;
        this.dataUltimaAlteracao = LocalDateTime.now();
    }

    public void aprovar(Long idAprovador, String observacaoEnfermeiro) {
        if (this.statusAlergia != StatusAlergia.INFORMADA) {
            throw new IllegalStateException(
                "Apenas alergias com status Informada podem ser aprovadas.");
        }
        this.statusAlergia          = StatusAlergia.APROVADA;
        this.idAprovador            = idAprovador;
        this.observacaoEnfermeiro   = observacaoEnfermeiro;
        this.dataUltimaAlteracao    = LocalDateTime.now();
    }

    public void rejeitar(Long idAprovador, String observacaoEnfermeiro) {
        if (this.statusAlergia != StatusAlergia.INFORMADA) {
            throw new IllegalStateException(
                "Apenas alergias com status Informada podem ser rejeitadas.");
        }
        this.statusAlergia          = StatusAlergia.REJEITADA;
        this.idAprovador            = idAprovador;
        this.observacaoEnfermeiro   = observacaoEnfermeiro;
        this.dataUltimaAlteracao    = LocalDateTime.now();
    }

    public void validarExclusao() {
        if (statusAlergia != StatusAlergia.INFORMADA && statusAlergia != StatusAlergia.REJEITADA) {
            throw new IllegalStateException(
                "Apenas alergias com status Informada ou Rejeitada podem ser excluídas.");
        }
    }
}
