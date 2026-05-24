package br.com.hsg.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum TipoSolicitacao {

    CADASTRAL("Cadastral"),
    ENDERECO("Endereço"),
    CLINICO("Clínico");

    private String descricao;
}
