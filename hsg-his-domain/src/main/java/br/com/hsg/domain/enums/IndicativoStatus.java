package br.com.hsg.domain.enums;

import lombok.Getter;

@Getter
public enum IndicativoStatus {

    A("A", "Ativo"),
    P("P", "Pendente"),
    I("I", "Inativo"),
    S("S", "Suspenso"),
    E("E", "Excluído");

    private final String valor;
    private final String descricao;

    IndicativoStatus(String valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    public static IndicativoStatus fromValor(String valor) {
        for (IndicativoStatus status : values()) {
            if (status.valor.equalsIgnoreCase(valor)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Valor inválido: " + valor);
    }
}