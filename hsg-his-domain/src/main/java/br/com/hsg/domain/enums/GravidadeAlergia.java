package br.com.hsg.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum GravidadeAlergia {
    L("Leve"),
    M("Moderada"),
    G("Grave"),
    A("Anafilática");

    private String descricao;

    public static GravidadeAlergia from(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }

        try {
            return GravidadeAlergia.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }

        for (GravidadeAlergia gravidade : values()) {
            if (gravidade.getDescricao().equalsIgnoreCase(valor)) {
                return gravidade;
            }
        }

        throw new IllegalArgumentException("Gravidade de alergia inválida: " + valor);
    }
}
