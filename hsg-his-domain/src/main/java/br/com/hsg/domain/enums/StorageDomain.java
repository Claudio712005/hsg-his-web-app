package br.com.hsg.domain.enums;

public enum StorageDomain {

    ANEXO_CLIENTE   ("/anexos/cliente"),
    ANEXO_CONSULTA  ("/anexos/consulta"),
    ANEXO_ANOTACAO  ("/anexos/anotacao"),
    EXAME_CONSULTA  ("/exames/consulta");

    private final String prefixoLogico;

    StorageDomain(String prefixoLogico) {
        this.prefixoLogico = prefixoLogico;
    }

    public String getPrefixoLogico() {
        return prefixoLogico;
    }

    public static StorageDomain pelaPrefixoDoPathLogico(String pathLogico) {
        if (pathLogico == null) {
            throw new IllegalArgumentException("Path lógico é obrigatório.");
        }
        for (StorageDomain d : values()) {
            if (pathLogico.startsWith(d.prefixoLogico + "/")) {
                return d;
            }
        }
        throw new IllegalArgumentException("Path lógico não pertence a nenhum domínio conhecido: " + pathLogico);
    }
}
