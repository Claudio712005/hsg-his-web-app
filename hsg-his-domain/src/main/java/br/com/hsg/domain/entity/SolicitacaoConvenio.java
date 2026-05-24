package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.StatusSolicitacao;
import br.com.hsg.domain.enums.TipoTitularidade;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_SOLIC_CONV", schema = "hsg")
public class SolicitacaoConvenio {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SOLIC_CONV")
    @SequenceGenerator(name = "SEQ_SOLIC_CONV", sequenceName = "SEQ_SOLIC_CONV", allocationSize = 1)
    @Column(name = "ID_SOLIC_CONV")
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PAC", nullable = false)
    private Paciente paciente;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PL_CONV", nullable = false)
    private PlanoConvenio plano;

    @Getter
    @Column(name = "NR_CART_ENC", length = 255)
    private String carteirinhaEnc;

    @Getter
    @Column(name = "NR_CART_MASC", length = 20)
    private String carteirinhaMascara;

    @Getter
    @Column(name = "DT_VALIDADE")
    private LocalDate dataValidade;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_TITULAR", length = 12, nullable = false)
    private TipoTitularidade tipoTitularidade;

    @Getter
    @Column(name = "DS_MOTIVO", length = 500)
    private String motivo;

    @Getter
    @Column(name = "SNP_PLANO_ATUAL", length = 200)
    private String snapshotPlanoAtual;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "SIT_SOLIC_CONV", length = 1, nullable = false)
    private StatusSolicitacao status;

    @Getter
    @Column(name = "ID_APROVADOR")
    private Long idAprovador;

    @Getter
    @Column(name = "DS_MOT_REJEICAO", length = 500)
    private String motivoRejeicao;

    @Getter
    @Column(name = "DT_CAD_SOLIC_CONV", nullable = false)
    private LocalDateTime dataCadastro;

    @Getter
    @Column(name = "DT_APROVACAO")
    private LocalDateTime dataAprovacao;

    @Getter
    @Column(name = "DT_ULT_ATU_SOLIC_CONV")
    private LocalDateTime dataUltimaAtualizacao;

    protected SolicitacaoConvenio() {}

    public static SolicitacaoConvenio solicitar(Paciente paciente, PlanoConvenio plano,
                                                String carteirinhaEnc, String carteirinhaMascara,
                                                LocalDate dataValidade, TipoTitularidade tipoTitularidade,
                                                String motivo, String snapshotPlanoAtual) {
        if (paciente == null) {
            throw new IllegalArgumentException("O paciente é obrigatório.");
        }
        if (plano == null) {
            throw new IllegalArgumentException("O plano é obrigatório.");
        }
        if (dataValidade != null && dataValidade.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A validade da carteirinha não pode ser uma data passada.");
        }
        SolicitacaoConvenio s = new SolicitacaoConvenio();
        s.paciente            = paciente;
        s.plano               = plano;
        s.carteirinhaEnc      = carteirinhaEnc;
        s.carteirinhaMascara  = carteirinhaMascara;
        s.dataValidade        = dataValidade;
        s.tipoTitularidade    = tipoTitularidade != null ? tipoTitularidade : TipoTitularidade.TITULAR;
        s.motivo              = motivo;
        s.snapshotPlanoAtual  = snapshotPlanoAtual;
        s.status              = StatusSolicitacao.P;
        s.dataCadastro        = LocalDateTime.now();
        return s;
    }

    public void aprovar(Long idAprovador) {
        if (this.status != StatusSolicitacao.P) {
            throw new IllegalStateException("Apenas solicitações pendentes podem ser aprovadas.");
        }
        this.status                = StatusSolicitacao.A;
        this.idAprovador           = idAprovador;
        this.dataAprovacao         = LocalDateTime.now();
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public void rejeitar(Long idAprovador, String motivoRejeicao) {
        if (this.status != StatusSolicitacao.P) {
            throw new IllegalStateException("Apenas solicitações pendentes podem ser rejeitadas.");
        }
        if (motivoRejeicao == null || motivoRejeicao.trim().isEmpty()) {
            throw new IllegalArgumentException("O motivo da rejeição é obrigatório.");
        }
        this.status                = StatusSolicitacao.R;
        this.idAprovador           = idAprovador;
        this.motivoRejeicao        = motivoRejeicao.trim();
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public void cancelar() {
        if (this.status != StatusSolicitacao.P) {
            throw new IllegalStateException("Apenas solicitações pendentes podem ser canceladas.");
        }
        this.status                = StatusSolicitacao.C;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public boolean isPendente() {
        return StatusSolicitacao.P.equals(status);
    }
}
