package br.com.hsg.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum AcaoAlergia {
    CRIADA("Criada"),
    EDITADA("Editada"),
    EXCLUIDA("Excluída"),
    APROVADA("Aprovada"),
    REJEITADA("Rejeitada");

    private String descricao;
}
