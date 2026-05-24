package br.com.hsg.domain.vo;

import lombok.Getter;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@AttributeOverrides({
        @AttributeOverride(name = "primeiroNome", column = @Column(name = "FRT_NM")),
        @AttributeOverride(name = "sobrenome", column = @Column(name = "LST_NM"))
})
public class NomeCompleto {

    @Getter
    private String primeiroNome;
    @Getter
    private String sobrenome;

    protected NomeCompleto() {}

    public NomeCompleto(String primeiroNome, String sobrenome) {
        this.primeiroNome = primeiroNome;
        this.sobrenome = sobrenome;
    }

    public String getNomeCompleto() {
        return primeiroNome + " " + sobrenome;
    }
}