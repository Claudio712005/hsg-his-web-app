package br.com.hsg.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum Sexo {
    M("M", "Masculino"),
    F("F", "Feminino"),
    O("O", "Outro");

    private String valor;
    private String descricao;

    public static Sexo getSexo(String valor) {
        for (Sexo sexo : Sexo.values()) {
            if (sexo.getValor().equalsIgnoreCase(valor)) {
                return sexo;
            }
        }

        throw new IllegalArgumentException("Valor de Sexo inválido: " + valor);
    }
}
