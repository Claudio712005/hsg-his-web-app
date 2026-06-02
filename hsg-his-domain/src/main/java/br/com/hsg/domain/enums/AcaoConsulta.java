package br.com.hsg.domain.enums;

public enum AcaoConsulta {

    AGENDADA("Agendamento"),
    CHECK_IN("Check-in"),
    REALIZADA("Atendimento realizado"),
    FALTOU("Falta registrada"),
    CANCELADA("Cancelamento");

    private final String descricao;

    AcaoConsulta(String descricao) { this.descricao = descricao; }

    public String getDescricao() { return descricao; }
}
