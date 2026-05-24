package br.com.hsg.web.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ContagemConvenioDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String convenio;
    private final long total;
    private final long planosComAdesao;
    private final BigDecimal receita;

    public ContagemConvenioDTO(String convenio, long total, long planosComAdesao, BigDecimal receita) {
        this.convenio = convenio;
        this.total = total;
        this.planosComAdesao = planosComAdesao;
        this.receita = receita != null ? receita : BigDecimal.ZERO;
    }

    public String getConvenio()        { return convenio; }
    public long getTotal()             { return total; }
    public long getPlanosComAdesao()   { return planosComAdesao; }
    public BigDecimal getReceita()     { return receita; }

    public BigDecimal getTicketMedio() {
        if (total <= 0) return BigDecimal.ZERO;
        return receita.divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
    }
}
