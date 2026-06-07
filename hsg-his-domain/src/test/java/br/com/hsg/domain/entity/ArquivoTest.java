package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.IndicativoStatus;
import br.com.hsg.domain.enums.StorageDomain;
import br.com.hsg.domain.enums.TipoResponsavel;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ArquivoTest {

    private static final String PATH = "/anexos/consulta/45/2026/06/abc.pdf";

    @Test
    public void registrar_caminhoFeliz_consulta_deveAtribuirIdConsulta() {
        Arquivo a = Arquivo.registrar(StorageDomain.ANEXO_CONSULTA, 45L, PATH,
                "exame.pdf", "application/pdf", 100L, null,
                7L, TipoResponsavel.MEDICO);
        assertEquals(Long.valueOf(45L), a.getIdConsulta());
        assertNull(a.getIdAnotacao());
        assertNull(a.getIdPaciente());
        assertEquals(StorageDomain.ANEXO_CONSULTA, a.getDominio());
        assertEquals(PATH, a.getPathLogico());
        assertEquals("exame.pdf", a.getNomeOriginal());
        assertEquals("application/pdf", a.getContentType());
        assertEquals(100L, a.getTamanhoBytes());
        assertEquals(IndicativoStatus.A, a.getStatus());
        assertNotNull(a.getDataUpload());
    }

    @Test
    public void registrar_anexoCliente_deveAtribuirIdPaciente() {
        Arquivo a = Arquivo.registrar(StorageDomain.ANEXO_CLIENTE, 123L,
                "/anexos/cliente/123/2026/06/x.png",
                "doc.png", "image/png", 50L, null, 5L, TipoResponsavel.PACIENTE);
        assertEquals(Long.valueOf(123L), a.getIdPaciente());
        assertNull(a.getIdConsulta());
        assertNull(a.getIdAnotacao());
    }

    @Test
    public void registrar_exameConsulta_deveAtribuirIdConsulta() {
        Arquivo a = Arquivo.registrar(StorageDomain.EXAME_CONSULTA, 9L,
                "/exames/consulta/9/2026/06/x.pdf",
                "exame.pdf", "application/pdf", 10L, null, 7L, TipoResponsavel.MEDICO);
        assertEquals(Long.valueOf(9L), a.getIdConsulta());
        assertNull(a.getIdAnotacao());
        assertNull(a.getIdPaciente());
    }

    @Test
    public void registrar_anotacao_deveAtribuirIdAnotacao() {
        Arquivo a = Arquivo.registrar(StorageDomain.ANEXO_ANOTACAO, 77L,
                "/anexos/anotacao/77/2026/06/x.pdf",
                "x.pdf", "application/pdf", 1L, null, 5L, TipoResponsavel.ENFERMEIRO);
        assertEquals(Long.valueOf(77L), a.getIdAnotacao());
        assertNull(a.getIdConsulta());
        assertNull(a.getIdPaciente());
    }

    @Test
    public void registrar_deveTrimSha256Vazio() {
        Arquivo a = Arquivo.registrar(StorageDomain.ANEXO_CONSULTA, 45L, PATH,
                "x.pdf", "application/pdf", 1L, "   ",
                7L, TipoResponsavel.MEDICO);
        assertNull(a.getSha256());
    }

    @Test
    public void inativar_devePassarStatusParaI() {
        Arquivo a = Arquivo.registrar(StorageDomain.ANEXO_CONSULTA, 45L, PATH,
                "x.pdf", "application/pdf", 1L, null, 7L, TipoResponsavel.MEDICO);
        a.inativar();
        assertEquals(IndicativoStatus.I, a.getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void registrar_deveLancarSeDominioNull() {
        Arquivo.registrar(null, 1L, PATH, "x.pdf", "application/pdf", 1L, null, 1L, TipoResponsavel.MEDICO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registrar_deveLancarSeOwnerIdInvalido() {
        Arquivo.registrar(StorageDomain.ANEXO_CONSULTA, 0L, PATH,
                "x.pdf", "application/pdf", 1L, null, 1L, TipoResponsavel.MEDICO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registrar_deveLancarSePathLogicoVazio() {
        Arquivo.registrar(StorageDomain.ANEXO_CONSULTA, 1L, "  ",
                "x.pdf", "application/pdf", 1L, null, 1L, TipoResponsavel.MEDICO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registrar_deveLancarSeNomeOriginalVazio() {
        Arquivo.registrar(StorageDomain.ANEXO_CONSULTA, 1L, PATH,
                " ", "application/pdf", 1L, null, 1L, TipoResponsavel.MEDICO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registrar_deveLancarSeContentTypeVazio() {
        Arquivo.registrar(StorageDomain.ANEXO_CONSULTA, 1L, PATH,
                "x.pdf", " ", 1L, null, 1L, TipoResponsavel.MEDICO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registrar_deveLancarSeTamanhoZero() {
        Arquivo.registrar(StorageDomain.ANEXO_CONSULTA, 1L, PATH,
                "x.pdf", "application/pdf", 0L, null, 1L, TipoResponsavel.MEDICO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registrar_deveLancarSeResponsavelNull() {
        Arquivo.registrar(StorageDomain.ANEXO_CONSULTA, 1L, PATH,
                "x.pdf", "application/pdf", 1L, null, null, TipoResponsavel.MEDICO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registrar_deveLancarSeTipoResponsavelInvalido() {
        Arquivo.registrar(StorageDomain.ANEXO_CONSULTA, 1L, PATH,
                "x.pdf", "application/pdf", 1L, null, 1L, TipoResponsavel.SISTEMA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registrar_deveLancarSeTipoResponsavelNull() {
        Arquivo.registrar(StorageDomain.ANEXO_CONSULTA, 1L, PATH,
                "x.pdf", "application/pdf", 1L, null, 1L, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registrar_deveLancarSeSha256LongoDemais() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Arquivo.MAX_SHA256 + 1; i++) sb.append('a');
        Arquivo.registrar(StorageDomain.ANEXO_CONSULTA, 1L, PATH,
                "x.pdf", "application/pdf", 1L, sb.toString(), 1L, TipoResponsavel.MEDICO);
    }
}
