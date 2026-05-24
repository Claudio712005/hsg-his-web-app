package br.com.hsg.domain.entity;

import br.com.hsg.domain.converter.IndicativoStatusConverter;
import br.com.hsg.domain.enums.IndicativoStatus;
import br.com.hsg.domain.enums.TipoTitularidade;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_PAC_CONV", schema = "hsg")
public class PacienteConvenio {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PAC_CONV")
    @SequenceGenerator(name = "SEQ_PAC_CONV", sequenceName = "SEQ_PAC_CONV", allocationSize = 1)
    @Column(name = "ID_PAC_CONV")
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
    @Column(name = "NR_CART_HASH", length = 64)
    private String carteirinhaHash;

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
    @Column(name = "ID_APROVADOR")
    private Long idAprovador;

    @Getter
    @Column(name = "DT_ADESAO", nullable = false)
    private LocalDateTime dataAdesao;

    @Getter
    @Column(name = "DT_CANCELAMENTO")
    private LocalDateTime dataCancelamento;

    @Getter
    @Convert(converter = IndicativoStatusConverter.class)
    @Column(name = "ST_PAC_CONV", nullable = false, length = 1)
    private IndicativoStatus status;

    @Getter
    @Column(name = "DT_CAD_PAC_CONV", nullable = false)
    private LocalDateTime dataCadastro;

    @Getter
    @Column(name = "DT_ULT_ATU_PAC_CONV")
    private LocalDateTime dataUltimaAtualizacao;

    protected PacienteConvenio() {}

    public static PacienteConvenio criar(Paciente paciente, PlanoConvenio plano,
                                         String carteirinhaHash, String carteirinhaEnc,
                                         String carteirinhaMascara, LocalDate dataValidade,
                                         TipoTitularidade tipoTitularidade, Long idAprovador) {
        if (paciente == null) {
            throw new IllegalArgumentException("O paciente é obrigatório.");
        }
        if (plano == null) {
            throw new IllegalArgumentException("O plano é obrigatório.");
        }
        PacienteConvenio pc = new PacienteConvenio();
        pc.paciente            = paciente;
        pc.plano               = plano;
        pc.carteirinhaHash     = carteirinhaHash;
        pc.carteirinhaEnc      = carteirinhaEnc;
        pc.carteirinhaMascara  = carteirinhaMascara;
        pc.dataValidade        = dataValidade;
        pc.tipoTitularidade    = tipoTitularidade != null ? tipoTitularidade : TipoTitularidade.TITULAR;
        pc.idAprovador         = idAprovador;
        pc.dataAdesao          = LocalDateTime.now();
        pc.status              = IndicativoStatus.A;
        pc.dataCadastro        = LocalDateTime.now();
        return pc;
    }

    public void cancelar() {
        this.status                = IndicativoStatus.I;
        this.dataCancelamento      = LocalDateTime.now();
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public boolean isAtivo() {
        return IndicativoStatus.A.equals(status);
    }
}
