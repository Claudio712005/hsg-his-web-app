package br.com.hsg.domain.vo;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Crm {

    @Getter
    @Column(name = "NR_CRM", length = 20)
    private String numero;

    @Getter
    @Column(name = "UF_CRM", length = 2)
    private String uf;

    protected Crm() {}

    public Crm(String numero, String uf) {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("Número do CRM é obrigatório.");
        }
        if (uf == null || uf.trim().isEmpty()) {
            throw new IllegalArgumentException("UF do CRM é obrigatória.");
        }
        this.numero = numero.trim();
        this.uf     = uf.trim().toUpperCase();
    }

    public String getFormatado() {
        return "CRM-" + uf + " " + numero;
    }
}
