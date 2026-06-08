package br.com.hsg.domain.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StorageDomainTest {

    @Test
    public void prefixos_devemSerUnicosEEstarPreenchidos() {
        for (StorageDomain d : StorageDomain.values()) {
            assertNotNull(d.getPrefixoLogico());
        }
        assertEquals("/anexos/cliente",  StorageDomain.ANEXO_CLIENTE.getPrefixoLogico());
        assertEquals("/anexos/consulta", StorageDomain.ANEXO_CONSULTA.getPrefixoLogico());
        assertEquals("/anexos/anotacao", StorageDomain.ANEXO_ANOTACAO.getPrefixoLogico());
        assertEquals("/exames/consulta", StorageDomain.EXAME_CONSULTA.getPrefixoLogico());
    }

    @Test
    public void pelaPrefixo_deveResolverDominioCorreto() {
        assertEquals(StorageDomain.ANEXO_CLIENTE,
                StorageDomain.pelaPrefixoDoPathLogico("/anexos/cliente/123/2026/06/x.pdf"));
        assertEquals(StorageDomain.ANEXO_CONSULTA,
                StorageDomain.pelaPrefixoDoPathLogico("/anexos/consulta/45/2026/06/x.png"));
        assertEquals(StorageDomain.ANEXO_ANOTACAO,
                StorageDomain.pelaPrefixoDoPathLogico("/anexos/anotacao/77/2026/06/x.jpg"));
        assertEquals(StorageDomain.EXAME_CONSULTA,
                StorageDomain.pelaPrefixoDoPathLogico("/exames/consulta/45/2026/06/x.pdf"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void pelaPrefixo_deveLancarParaPathDesconhecido() {
        StorageDomain.pelaPrefixoDoPathLogico("/qualquer/outro/path.pdf");
    }

    @Test(expected = IllegalArgumentException.class)
    public void pelaPrefixo_deveLancarParaNull() {
        StorageDomain.pelaPrefixoDoPathLogico(null);
    }
}
