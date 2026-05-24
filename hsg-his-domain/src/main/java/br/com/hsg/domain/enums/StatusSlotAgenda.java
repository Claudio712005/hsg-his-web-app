package br.com.hsg.domain.enums;

import lombok.Getter;

@Getter
public enum StatusSlotAgenda {

    LIVRE("LIVRE", "Livre"),
    RESERVADO("RESERVADO", "Reservado"),
    BLOQUEADO("BLOQUEADO", "Bloqueado"),
    CANCELADO("CANCELADO", "Cancelado");

    private final String valor;
    private final String descricao;

    StatusSlotAgenda(String valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    public static StatusSlotAgenda fromValor(String valor) {
        for (StatusSlotAgenda s : values()) {
            if (s.valor.equalsIgnoreCase(valor)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status de slot inválido: " + valor);
    }
}
