package br.com.hsg.domain.vo;

import lombok.Getter;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class CPF {

    @Getter
    private String valor;

    protected CPF() {}

    public CPF(String valor) {
        if (valor == null || !valor.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF inválido");
        }
        this.valor = valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CPF)) return false;
        CPF cpf = (CPF) o;
        return Objects.equals(valor, cpf.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }
}
