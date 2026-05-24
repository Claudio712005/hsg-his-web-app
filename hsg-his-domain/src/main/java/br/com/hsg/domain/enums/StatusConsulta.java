package br.com.hsg.domain.enums;

import lombok.Getter;

@Getter
public enum StatusConsulta {

    AGENDADA("AGENDADA", "Agendada"),
    CONFIRMADA("CONFIRMADA", "Confirmada"),
    REALIZADA("REALIZADA", "Realizada"),
    CANCELADA("CANCELADA", "Cancelada"),
    FALTOU("FALTOU", "Faltou");

    private final String valor;
    private final String descricao;

    StatusConsulta(String valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    public static StatusConsulta fromValor(String valor) {
        for (StatusConsulta s : values()) {
            if (s.valor.equalsIgnoreCase(valor)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status de consulta inválido: " + valor);
    }
}
