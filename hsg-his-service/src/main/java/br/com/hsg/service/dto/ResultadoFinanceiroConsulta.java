package br.com.hsg.service.dto;

import br.com.hsg.domain.enums.TipoAtendimentoConsulta;

import java.io.Serializable;
import java.math.BigDecimal;

public class ResultadoFinanceiroConsulta implements Serializable {

    private static final long serialVersionUID = 1L;

    private final TipoAtendimentoConsulta tipoAtendimento;
    private final BigDecimal valorConsulta;
    private final BigDecimal valorCopagamento;
    private final BigDecimal valorCoberturaConvenio;
    private final boolean convenioDisponivel;
    private final boolean emCarencia;
    private final String observacao;

    public ResultadoFinanceiroConsulta(TipoAtendimentoConsulta tipoAtendimento,
                                       BigDecimal valorConsulta,
                                       BigDecimal valorCopagamento,
                                       BigDecimal valorCoberturaConvenio,
                                       boolean convenioDisponivel,
                                       boolean emCarencia,
                                       String observacao) {
        this.tipoAtendimento        = tipoAtendimento;
        this.valorConsulta          = valorConsulta;
        this.valorCopagamento       = valorCopagamento;
        this.valorCoberturaConvenio = valorCoberturaConvenio;
        this.convenioDisponivel     = convenioDisponivel;
        this.emCarencia             = emCarencia;
        this.observacao             = observacao;
    }

    public TipoAtendimentoConsulta getTipoAtendimento() { return tipoAtendimento; }
    public BigDecimal getValorConsulta()                { return valorConsulta; }
    public BigDecimal getValorCopagamento()             { return valorCopagamento; }
    public BigDecimal getValorCoberturaConvenio()       { return valorCoberturaConvenio; }
    public boolean isConvenioDisponivel()               { return convenioDisponivel; }
    public boolean isEmCarencia()                       { return emCarencia; }
    public String getObservacao()                       { return observacao; }
}
