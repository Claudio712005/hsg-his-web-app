package br.com.hsg.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public enum TipoSanguineoEnum {
    A_POS("A+"),
    A_NEG("A-"),
    B_POS("B+"),
    B_NEG("B-"),
    AB_POS("AB+"),
    AB_NEG("AB-"),
    O_POS("O+"),
    O_NEG("O-");

    @Getter
    private String descricao;

    public static TipoSanguineoEnum getTipoSanguineo(String descricao) {
        for (TipoSanguineoEnum tipo : TipoSanguineoEnum.values()) {
            if (tipo.getDescricao().equalsIgnoreCase(descricao)) {
                return tipo;
            }
        }

        throw new IllegalArgumentException("Valor de Tipo Sanguíneo inválido: " + descricao);
    }

    public boolean podeReceberDe(TipoSanguineoEnum doador) {
        switch (this) {
            case O_NEG:
                return doador == O_NEG;

            case O_POS:
                return doador == O_NEG || doador == O_POS;

            case A_NEG:
                return doador == O_NEG || doador == A_NEG;

            case A_POS:
                return doador == O_NEG || doador == O_POS ||
                        doador == A_NEG || doador == A_POS;

            case B_NEG:
                return doador == O_NEG || doador == B_NEG;

            case B_POS:
                return doador == O_NEG || doador == O_POS ||
                        doador == B_NEG || doador == B_POS;

            case AB_NEG:
                return doador == O_NEG || doador == A_NEG ||
                        doador == B_NEG || doador == AB_NEG;

            case AB_POS:
                return true;
        }
        return false;
    }

    public static TipoSanguineoEnum from(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }

        try {
            return TipoSanguineoEnum.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }

        for (TipoSanguineoEnum tipo : values()) {
            if (tipo.getDescricao().equalsIgnoreCase(valor)) {
                return tipo;
            }
        }

        throw new IllegalArgumentException("Tipo sanguíneo inválido: " + valor);
    }
}