package br.com.hsg.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum CategoriaCoren {
    ENF("Enfermeiro"),
    TEC("Técnico de Enfermagem"),
    AUX("Auxiliar de Enfermagem");

    private String descricao;
}
