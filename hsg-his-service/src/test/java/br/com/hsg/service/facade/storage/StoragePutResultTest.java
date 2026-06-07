package br.com.hsg.service.facade.storage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StoragePutResultTest {

    @Test
    public void deveExporCamposExatos() {
        StoragePutResult r = new StoragePutResult(
                "/anexos/consulta/45/2026/06/abc.pdf", 12345L, "application/pdf", "etag-xyz");
        assertEquals("/anexos/consulta/45/2026/06/abc.pdf", r.getPathLogico());
        assertEquals(12345L, r.getSizeBytes());
        assertEquals("application/pdf", r.getContentType());
        assertEquals("etag-xyz", r.getEtag());
    }
}
