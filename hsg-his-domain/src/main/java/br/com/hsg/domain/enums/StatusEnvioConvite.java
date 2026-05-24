package br.com.hsg.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum StatusEnvioConvite {
    ENVIADO("Enviado"),
    ERRO("Erro no envio"),
    EXPIRADO("Expirado"),
    ACEITO("Aceito");

    private String descricao;
}
