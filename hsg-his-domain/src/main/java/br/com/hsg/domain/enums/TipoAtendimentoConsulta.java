package br.com.hsg.domain.enums;

import lombok.Getter;

@Getter
public enum TipoAtendimentoConsulta {

    CONVENIO("CONVENIO", "Convênio"),
    PARTICULAR("PARTICULAR", "Particular");

    private final String valor;
    private final String descricao;

    TipoAtendimentoConsulta(String valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    public static TipoAtendimentoConsulta fromValor(String valor) {
        for (TipoAtendimentoConsulta t : values()) {
            if (t.valor.equalsIgnoreCase(valor)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de atendimento inválido: " + valor);
    }
}
