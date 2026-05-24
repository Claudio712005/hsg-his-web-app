package br.com.hsg.domain.enums;

import lombok.Getter;

import java.time.DayOfWeek;

@Getter
public enum DiaSemana {

    SEGUNDA(1, "Segunda-feira", "Seg"),
    TERCA(2,   "Terça-feira",   "Ter"),
    QUARTA(3,  "Quarta-feira",  "Qua"),
    QUINTA(4,  "Quinta-feira",  "Qui"),
    SEXTA(5,   "Sexta-feira",   "Sex"),
    SABADO(6,  "Sábado",        "Sáb"),
    DOMINGO(7, "Domingo",       "Dom");

    private final int valor;
    private final String descricao;
    private final String abreviacao;

    DiaSemana(int valor, String descricao, String abreviacao) {
        this.valor      = valor;
        this.descricao  = descricao;
        this.abreviacao = abreviacao;
    }

    public static DiaSemana fromValor(int valor) {
        for (DiaSemana d : values()) {
            if (d.valor == valor) return d;
        }
        throw new IllegalArgumentException("Dia da semana inválido: " + valor);
    }

    public static DiaSemana fromDayOfWeek(DayOfWeek dow) {
        return dow != null ? fromValor(dow.getValue()) : null;
    }

    public DayOfWeek toDayOfWeek() {
        return DayOfWeek.of(this.valor);
    }
}
