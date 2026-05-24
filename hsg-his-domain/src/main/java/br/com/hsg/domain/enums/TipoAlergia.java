package br.com.hsg.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum TipoAlergia {

    A("Alimentar"),
    M("Medicamentos"),
    AM("Ambiental"),
    O("Outra");

    private String descricao;

    public static TipoAlergia from(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }

        try {
            return TipoAlergia.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }

        for (TipoAlergia tipo : values()) {
            if (tipo.getDescricao().equalsIgnoreCase(valor)) {
                return tipo;
            }
        }

        throw new IllegalArgumentException("Tipo alergia inválido: " + valor);
    }
}
