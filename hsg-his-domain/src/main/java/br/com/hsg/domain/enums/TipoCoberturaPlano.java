package br.com.hsg.domain.enums;

import lombok.Getter;

@Getter
public enum TipoCoberturaPlano {

    AMBULATORIAL("AMBULATORIAL", "Ambulatorial"),
    HOSPITALAR("HOSPITALAR", "Hospitalar"),
    OBSTETRICO("OBSTETRICO", "Obstétrico"),
    REFERENCIA("REFERENCIA", "Referência"),
    COMPLETO("COMPLETO", "Completo"),
    ODONTOLOGICO("ODONTOLOGICO", "Odontológico");

    private final String valor;
    private final String descricao;

    TipoCoberturaPlano(String valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    public static TipoCoberturaPlano fromValor(String valor) {
        for (TipoCoberturaPlano t : values()) {
            if (t.valor.equalsIgnoreCase(valor)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de cobertura inválido: " + valor);
    }
}
