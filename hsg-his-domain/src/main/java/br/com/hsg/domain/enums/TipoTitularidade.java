package br.com.hsg.domain.enums;

import lombok.Getter;

@Getter
public enum TipoTitularidade {

    TITULAR("TITULAR", "Titular"),
    DEPENDENTE("DEPENDENTE", "Dependente");

    private final String valor;
    private final String descricao;

    TipoTitularidade(String valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    public static TipoTitularidade fromValor(String valor) {
        for (TipoTitularidade t : values()) {
            if (t.valor.equalsIgnoreCase(valor)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de titularidade inválido: " + valor);
    }
}
