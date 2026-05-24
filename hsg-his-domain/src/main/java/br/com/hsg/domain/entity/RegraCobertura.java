package br.com.hsg.domain.entity;

import br.com.hsg.domain.converter.IndicativoStatusConverter;
import br.com.hsg.domain.enums.IndicativoStatus;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_REG_COB", schema = "hsg")
public class RegraCobertura {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_REG_COB")
    @SequenceGenerator(name = "SEQ_REG_COB", sequenceName = "SEQ_REG_COB", allocationSize = 1)
    @Column(name = "ID_REG_COB")
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PL_CONV", nullable = false)
    private PlanoConvenio plano;

    @Getter
    @Column(name = "DS_PROCEDIMENTO", length = 200, nullable = false)
    private String procedimento;

    @Getter
    @Column(name = "DS_CATEGORIA", length = 80)
    private String categoria;

    @Getter
    @Column(name = "NR_CARENCIA_DIAS", nullable = false)
    private Integer carenciaDias;

    @Getter
    @Column(name = "VL_PCT_COPAG", precision = 5, scale = 2, nullable = false)
    private BigDecimal percentualCopagamento;

    @Column(name = "FL_COBERTURA", length = 1, nullable = false)
    private String cobertura;

    @Getter
    @Column(name = "DS_OBSERVACAO", length = 500)
    private String observacao;

    @Getter
    @Convert(converter = IndicativoStatusConverter.class)
    @Column(name = "ST_REG_COB", nullable = false, length = 1)
    private IndicativoStatus status;

    @Getter
    @Column(name = "DT_CAD_REG_COB", nullable = false)
    private LocalDateTime dataCadastro;

    @Getter
    @Column(name = "DT_ULT_ATU_REG_COB")
    private LocalDateTime dataUltimaAtualizacao;

    protected RegraCobertura() {}

    public static RegraCobertura criar(PlanoConvenio plano, String procedimento, String categoria,
                                        Integer carenciaDias, BigDecimal percentualCopagamento,
                                        boolean cobertura, String observacao) {
        validar(plano, procedimento, carenciaDias, percentualCopagamento);
        RegraCobertura r = new RegraCobertura();
        r.plano                 = plano;
        r.procedimento          = procedimento.trim();
        r.categoria             = categoria;
        r.carenciaDias          = carenciaDias;
        r.percentualCopagamento = percentualCopagamento;
        r.cobertura             = cobertura ? "S" : "N";
        r.observacao            = observacao;
        r.status                = IndicativoStatus.A;
        r.dataCadastro          = LocalDateTime.now();
        return r;
    }

    public static void validar(PlanoConvenio plano, String procedimento,
                               Integer carenciaDias, BigDecimal percentualCopagamento) {
        if (plano == null) {
            throw new IllegalArgumentException("O plano é obrigatório.");
        }
        if (procedimento == null || procedimento.trim().isEmpty()) {
            throw new IllegalArgumentException("O procedimento é obrigatório.");
        }
        if (carenciaDias == null || carenciaDias < 0) {
            throw new IllegalArgumentException("A carência em dias deve ser maior ou igual a zero.");
        }
        if (percentualCopagamento == null
                || percentualCopagamento.compareTo(BigDecimal.ZERO) < 0
                || percentualCopagamento.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("O percentual de copagamento deve estar entre 0 e 100.");
        }
    }

    public void atualizar(String procedimento, String categoria,
                          Integer carenciaDias, BigDecimal percentualCopagamento,
                          boolean cobertura, String observacao) {
        validar(this.plano, procedimento, carenciaDias, percentualCopagamento);
        this.procedimento          = procedimento.trim();
        this.categoria             = categoria;
        this.carenciaDias          = carenciaDias;
        this.percentualCopagamento = percentualCopagamento;
        this.cobertura             = cobertura ? "S" : "N";
        this.observacao            = observacao;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public boolean isCoberto() {
        return "S".equalsIgnoreCase(this.cobertura);
    }

    public void ativar() {
        this.status = IndicativoStatus.A;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public void inativar() {
        this.status = IndicativoStatus.I;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public boolean isAtivo() {
        return IndicativoStatus.A.equals(status);
    }
}
