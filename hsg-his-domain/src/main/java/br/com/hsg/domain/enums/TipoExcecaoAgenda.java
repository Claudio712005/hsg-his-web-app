package br.com.hsg.domain.enums;

import lombok.Getter;

@Getter
public enum TipoExcecaoAgenda {

    FERIAS("FERIAS", "Férias"),
    BLOQUEIO("BLOQUEIO", "Bloqueio"),
    EVENTO("EVENTO", "Evento");

    private final String valor;
    private final String descricao;

    TipoExcecaoAgenda(String valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    public static TipoExcecaoAgenda fromValor(String valor) {
        for (TipoExcecaoAgenda t : values()) {
            if (t.valor.equalsIgnoreCase(valor)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de exceção inválido: " + valor);
    }
}
