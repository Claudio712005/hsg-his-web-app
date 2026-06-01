package br.com.hsg.domain.enums;

public enum TipoDestinatarioNotificacao {

    PACIENTE("Paciente"),
    MEDICO("Médico"),
    ENFERMEIRO("Enfermeiro"),
    ADMIN("Administrador");

    private final String descricao;

    TipoDestinatarioNotificacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
