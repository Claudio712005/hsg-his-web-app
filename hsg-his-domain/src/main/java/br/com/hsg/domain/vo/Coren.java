package br.com.hsg.domain.vo;

import br.com.hsg.domain.enums.CategoriaCoren;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class Coren {

    @Getter
    @Column(name = "NR_COREN", length = 20)
    private String numero;

    @Getter
    @Column(name = "UF_COREN", length = 2)
    private String uf;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "CAT_COREN", length = 3)
    private CategoriaCoren categoria;

    protected Coren() {}

    public Coren(String numero, String uf, CategoriaCoren categoria) {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("Número do COREN é obrigatório.");
        }
        if (uf == null || uf.trim().isEmpty()) {
            throw new IllegalArgumentException("UF do COREN é obrigatória.");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria do COREN é obrigatória.");
        }
        this.numero    = numero.trim();
        this.uf        = uf.trim().toUpperCase();
        this.categoria = categoria;
    }

    public String getFormatado() {
        return "COREN-" + uf + " " + numero + "/" + (categoria != null ? categoria.name() : "ENF");
    }
}
