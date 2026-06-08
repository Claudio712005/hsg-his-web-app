package br.com.hsg.domain.enums;

public enum TipoNotificacao {

    INFO("Informativo"),
    SUCESSO("Sucesso"),
    ALERTA("Alerta"),
    ERRO("Erro");

    private final String descricao;

    TipoNotificacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
