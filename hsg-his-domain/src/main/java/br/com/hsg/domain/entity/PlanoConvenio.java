package br.com.hsg.domain.entity;

import br.com.hsg.domain.converter.IndicativoStatusConverter;
import br.com.hsg.domain.enums.IndicativoStatus;
import br.com.hsg.domain.enums.TipoCoberturaPlano;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_PL_CONV", schema = "hsg")
public class PlanoConvenio {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PL_CONV")
    @SequenceGenerator(name = "SEQ_PL_CONV", sequenceName = "SEQ_PL_CONV", allocationSize = 1)
    @Column(name = "ID_PL_CONV")
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONV", nullable = false)
    private Convenio convenio;

    @Getter
    @Column(name = "NM_PL_CONV", length = 150, nullable = false)
    private String nome;

    @Getter
    @Column(name = "CD_PL_CONV", length = 50)
    private String codigo;

    @Getter
    @Column(name = "DS_PL_CONV", length = 500)
    private String descricao;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_COBERTURA", length = 20, nullable = false)
    private TipoCoberturaPlano tipoCobertura;

    @Getter
    @Column(name = "VL_MENSALIDADE", precision = 10, scale = 2)
    private BigDecimal valorMensalidade;

    @Column(name = "FL_ACOMOD_INDIVIDUAL", length = 1, nullable = false)
    private String acomodacaoIndividual;

    @Getter
    @Convert(converter = IndicativoStatusConverter.class)
    @Column(name = "ST_PL_CONV", nullable = false, length = 1)
    private IndicativoStatus status;

    @Getter
    @Column(name = "DT_CAD_PL_CONV", nullable = false)
    private LocalDateTime dataCadastro;

    @Getter
    @Column(name = "DT_ULT_ATU_PL_CONV")
    private LocalDateTime dataUltimaAtualizacao;

    protected PlanoConvenio() {}

    public static PlanoConvenio criar(Convenio convenio, String nome, String codigo,
                                       String descricao, TipoCoberturaPlano tipoCobertura,
                                       BigDecimal valorMensalidade, boolean acomodacaoIndividual) {
        validar(convenio, nome, tipoCobertura);
        PlanoConvenio p = new PlanoConvenio();
        p.convenio              = convenio;
        p.nome                  = nome.trim();
        p.codigo                = codigo;
        p.descricao             = descricao;
        p.tipoCobertura         = tipoCobertura;
        p.valorMensalidade      = valorMensalidade;
        p.acomodacaoIndividual  = acomodacaoIndividual ? "S" : "N";
        p.status                = IndicativoStatus.A;
        p.dataCadastro          = LocalDateTime.now();
        return p;
    }

    public static void validar(Convenio convenio, String nome, TipoCoberturaPlano tipoCobertura) {
        if (convenio == null) {
            throw new IllegalArgumentException("O convênio é obrigatório.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do plano é obrigatório.");
        }
        if (tipoCobertura == null) {
            throw new IllegalArgumentException("O tipo de cobertura é obrigatório.");
        }
    }

    public void atualizar(String nome, String codigo, String descricao,
                          TipoCoberturaPlano tipoCobertura,
                          BigDecimal valorMensalidade, boolean acomodacaoIndividual) {
        validar(this.convenio, nome, tipoCobertura);
        this.nome                 = nome.trim();
        this.codigo               = codigo;
        this.descricao            = descricao;
        this.tipoCobertura        = tipoCobertura;
        this.valorMensalidade     = valorMensalidade;
        this.acomodacaoIndividual = acomodacaoIndividual ? "S" : "N";
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public boolean isAcomodacaoIndividual() {
        return "S".equalsIgnoreCase(this.acomodacaoIndividual);
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
