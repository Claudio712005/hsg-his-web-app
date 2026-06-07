package br.com.hsg.service.impl.storage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StorageGuardTest {

    @Test
    public void validarContentType_deveAceitarWhitelist() {
        StorageGuard.validarContentType("application/pdf");
        StorageGuard.validarContentType("image/jpeg");
        StorageGuard.validarContentType("image/png");
        StorageGuard.validarContentType("image/webp");
        StorageGuard.validarContentType("APPLICATION/PDF");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validarContentType_deveRejeitarNaoListado() {
        StorageGuard.validarContentType("application/zip");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validarContentType_deveRejeitarNull() {
        StorageGuard.validarContentType(null);
    }

    @Test
    public void validarTamanho_deveAceitarDentroDoLimite() {
        StorageGuard.validarTamanho(1L, 10L);
        StorageGuard.validarTamanho(10L, 10L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validarTamanho_deveRejeitarZero() {
        StorageGuard.validarTamanho(0L, 10L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validarTamanho_deveRejeitarAcimaDoLimite() {
        StorageGuard.validarTamanho(11L, 10L);
    }

    @Test
    public void sanitizeFilename_deveRemoverPathSeparadores() {
        assertEquals("arquivo.pdf", StorageGuard.sanitizeFilename("/etc/passwd/../arquivo.pdf"));
        assertEquals("arquivo.pdf", StorageGuard.sanitizeFilename("C:\\Users\\x\\arquivo.pdf"));
    }

    @Test
    public void sanitizeFilename_deveRemoverCaracteresInvalidos() {
        assertEquals("nome_seguro.pdf", StorageGuard.sanitizeFilename("nome seguro.pdf"));
        assertEquals("nome_acentuado.png", StorageGuard.sanitizeFilename("nomeéacentuado.png"));
    }

    @Test
    public void sanitizeFilename_deveColapsarPontosEliminarPrefixoOculto() {
        assertEquals("oculto.pdf", StorageGuard.sanitizeFilename("...oculto.pdf"));
        assertEquals("a.b.pdf",    StorageGuard.sanitizeFilename("a..b...pdf"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void sanitizeFilename_deveLancarSeVazioApos() {
        StorageGuard.sanitizeFilename("///");
    }

    @Test(expected = IllegalArgumentException.class)
    public void sanitizeFilename_deveLancarSeNull() {
        StorageGuard.sanitizeFilename(null);
    }

    @Test
    public void validarMagicBytes_pdf_assinaturaCorreta() {
        byte[] pdf = new byte[]{0x25, 0x50, 0x44, 0x46, 0x2D, 0x31, 0x2E};
        StorageGuard.validarMagicBytes(pdf, "application/pdf");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validarMagicBytes_pdf_assinaturaErrada() {
        byte[] fake = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00};
        StorageGuard.validarMagicBytes(fake, "application/pdf");
    }

    @Test
    public void validarMagicBytes_jpegPngWebp_assinaturasCorretas() {
        StorageGuard.validarMagicBytes(new byte[]{(byte)0xFF,(byte)0xD8,(byte)0xFF,(byte)0xE0}, "image/jpeg");
        StorageGuard.validarMagicBytes(
                new byte[]{(byte)0x89,0x50,0x4E,0x47,0x0D,0x0A,0x1A,0x0A}, "image/png");
        StorageGuard.validarMagicBytes(
                new byte[]{'R','I','F','F',0x00,0x00,0x00,0x00,'W','E','B','P'}, "image/webp");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validarMagicBytes_webp_assinaturaErrada() {
        StorageGuard.validarMagicBytes(
                new byte[]{'R','I','F','F',0,0,0,0,'X','X','X','X'}, "image/webp");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validarMagicBytes_contentTypeNaoMapeado() {
        StorageGuard.validarMagicBytes(new byte[]{1,2,3,4}, "application/zip");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validarMagicBytes_headPequenoDemais() {
        StorageGuard.validarMagicBytes(new byte[]{1,2}, "application/pdf");
    }

    @Test
    public void validar_facade_caminhoFeliz() {
        byte[] pdf = new byte[]{0x25, 0x50, 0x44, 0x46, 0x2D};
        StorageGuard.validar("exame.pdf", "application/pdf", 1024L, 2048L, pdf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validar_facade_contentTypeForaDaWhitelist() {
        StorageGuard.validar("x.zip", "application/zip", 10L, 100L, new byte[]{1,2,3,4});
    }
}
