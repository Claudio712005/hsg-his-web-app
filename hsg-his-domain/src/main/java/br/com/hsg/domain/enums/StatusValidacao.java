package br.com.hsg.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum StatusValidacao {

    IF("Informado"),
    VA("Validado"),
    RT("Rejeitado");

    private String descricao;

}
