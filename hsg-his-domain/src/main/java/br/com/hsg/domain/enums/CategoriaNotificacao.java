package br.com.hsg.domain.enums;

public enum CategoriaNotificacao {

    CONSULTA("Consulta"),
    CONVENIO("Convênio"),
    AGENDA("Agenda"),
    SISTEMA("Sistema");

    private final String descricao;

    CategoriaNotificacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
