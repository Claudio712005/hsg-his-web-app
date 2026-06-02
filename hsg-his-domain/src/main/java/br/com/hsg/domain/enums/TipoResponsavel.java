package br.com.hsg.domain.enums;

public enum TipoResponsavel {

    PACIENTE("Paciente"),
    MEDICO("Médico"),
    ENFERMEIRO("Enfermeiro"),
    ADMIN("Administração"),
    SISTEMA("Sistema");

    private final String descricao;

    TipoResponsavel(String descricao) { this.descricao = descricao; }

    public String getDescricao() { return descricao; }
}
