package br.com.hsg.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum TipoProfissional {
    MEDICO("Médico"),
    ENFERMEIRO("Enfermeiro");

    private String descricao;
}
