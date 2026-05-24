package br.com.hsg.domain.vo;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
public class Telefone {

    @Getter
    private String valor;

    protected Telefone() {}

    public Telefone(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Telefone não pode ser nulo");
        }

        if (valor.length() != 11) {
            throw new IllegalArgumentException("Telefone deve conter 11 dígitos");
        }

        this.valor = valor;
    }
}
