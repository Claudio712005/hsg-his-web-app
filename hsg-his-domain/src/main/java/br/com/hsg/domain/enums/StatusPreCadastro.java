package br.com.hsg.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum StatusPreCadastro {
    PENDENTE("Pendente"),
    CONCLUIDO("Concluído"),
    EXPIRADO("Expirado");

    private String descricao;
}
