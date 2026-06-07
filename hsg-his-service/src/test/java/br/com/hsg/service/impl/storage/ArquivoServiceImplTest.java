package br.com.hsg.service.impl.storage;

import br.com.hsg.dao.ArquivoDAO;
import br.com.hsg.dao.ConsultaAnotacaoDAO;
import br.com.hsg.dao.ConsultaDAO;
import br.com.hsg.domain.entity.Arquivo;
import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.entity.ConsultaAnotacao;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.domain.enums.IndicativoStatus;
import br.com.hsg.domain.enums.StatusConsulta;
import br.com.hsg.domain.enums.StorageDomain;
import br.com.hsg.domain.enums.TipoResponsavel;
import br.com.hsg.service.facade.storage.ObjectStorageService;
import br.com.hsg.service.facade.storage.StoragePathResolver;
import br.com.hsg.service.facade.storage.StoragePutResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URL;
import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ArquivoServiceImplTest {

    @Mock private ArquivoDAO            arquivoDAO;
    @Mock private ConsultaDAO           consultaDAO;
    @Mock private ConsultaAnotacaoDAO   anotacaoDAO;
    @Mock private StoragePathResolver   pathResolver;
    @Mock private ObjectStorageService  storage;

    @InjectMocks private ArquivoServiceImpl service;

    private Consulta consulta;
    private Medico   medico;
    private Paciente paciente;

    private static final byte[] PDF_PAYLOAD = new byte[]{
            0x25, 0x50, 0x44, 0x46, 0x2D, 0x31, 0x2E, 0x34,
            0x0A, (byte)0xFF, 0x00, 0x11, 0x22, 0x33, 0x44, 0x55,
            0x66, 0x77, (byte)0x88
    };

    @Before
    public void setUp() {
        medico   = mock(Medico.class);
        paciente = mock(Paciente.class);
        consulta = mock(Consulta.class);

        when(medico.getId()).thenReturn(7L);
        when(paciente.getId()).thenReturn(100L);
        when(consulta.getMedico()).thenReturn(medico);
        when(consulta.getPaciente()).thenReturn(paciente);
        when(consulta.getStatus()).thenReturn(StatusConsulta.CONFIRMADA);
        when(consultaDAO.buscarPorIdComMedico(1L)).thenReturn(consulta);

        when(pathResolver.buildLogicalPath(any(StorageDomain.class), anyLong(), anyString()))
                .thenAnswer(i -> {
                    StorageDomain d = i.getArgument(0);
                    long owner = i.getArgument(1);
                    return d.getPrefixoLogico() + "/" + owner + "/2026/06/abc.pdf";
                });
        when(storage.put(anyString(), any(), anyLong(), anyString()))
                .thenAnswer(i -> new StoragePutResult(i.getArgument(0), i.getArgument(2),
                        i.getArgument(3), "etag-x"));
        when(arquivoDAO.salvar(any(Arquivo.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    public void anexarEmConsulta_medico_consultaPropria_deveSalvar() {
        Arquivo a = service.anexarEmConsulta(1L, PDF_PAYLOAD, "application/pdf", "exame.pdf",
                7L, TipoResponsavel.MEDICO);

        assertNotNull(a);
        assertEquals(StorageDomain.ANEXO_CONSULTA, a.getDominio());
        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        verify(storage).put(pathCap.capture(), any(), eq((long) PDF_PAYLOAD.length), eq("application/pdf"));
        assertEquals("/anexos/consulta/1/2026/06/abc.pdf", pathCap.getValue());
    }

    @Test(expected = IllegalStateException.class)
    public void anexarEmConsulta_medico_consultaDeOutro_deveRecusar() {
        service.anexarEmConsulta(1L, PDF_PAYLOAD, "application/pdf", "exame.pdf",
                999L, TipoResponsavel.MEDICO);
    }

    @Test(expected = IllegalStateException.class)
    public void anexarEmConsulta_consultaCancelada_deveRecusar() {
        when(consulta.getStatus()).thenReturn(StatusConsulta.CANCELADA);
        service.anexarEmConsulta(1L, PDF_PAYLOAD, "application/pdf", "x.pdf",
                7L, TipoResponsavel.MEDICO);
    }

    @Test(expected = IllegalStateException.class)
    public void anexarEmConsulta_paciente_deveRecusar() {
        service.anexarEmConsulta(1L, PDF_PAYLOAD, "application/pdf", "x.pdf",
                100L, TipoResponsavel.PACIENTE);
    }

    @Test
    public void anexarEmConsulta_enfermeiro_deveSalvar() {
        service.anexarEmConsulta(1L, PDF_PAYLOAD, "application/pdf", "x.pdf",
                33L, TipoResponsavel.ENFERMEIRO);
        verify(arquivoDAO).salvar(any(Arquivo.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void anexarEmConsulta_contentTypeForaDaWhitelist_deveRecusar() {
        service.anexarEmConsulta(1L, PDF_PAYLOAD, "application/zip", "x.zip",
                33L, TipoResponsavel.ENFERMEIRO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void anexarEmConsulta_payloadVazio_deveRecusar() {
        service.anexarEmConsulta(1L, new byte[0], "application/pdf", "x.pdf",
                33L, TipoResponsavel.ENFERMEIRO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void anexarEmConsulta_magicBytesIncorretos_deveRecusar() {
        byte[] fake = new byte[]{0,0,0,0,0,0};
        service.anexarEmConsulta(1L, fake, "application/pdf", "x.pdf",
                33L, TipoResponsavel.ENFERMEIRO);
    }

    @Test
    public void anexarExameEmConsulta_paciente_consultaAgendada_deveSalvar() {
        when(consulta.getStatus()).thenReturn(StatusConsulta.AGENDADA);

        Arquivo a = service.anexarExameEmConsulta(1L, PDF_PAYLOAD, "application/pdf", "exame.pdf",
                100L, TipoResponsavel.PACIENTE);

        assertEquals(StorageDomain.EXAME_CONSULTA, a.getDominio());
        verify(arquivoDAO).salvar(any(Arquivo.class));
    }

    @Test(expected = IllegalStateException.class)
    public void anexarExameEmConsulta_paciente_consultaRealizada_deveRecusar() {
        when(consulta.getStatus()).thenReturn(StatusConsulta.REALIZADA);
        service.anexarExameEmConsulta(1L, PDF_PAYLOAD, "application/pdf", "x.pdf",
                100L, TipoResponsavel.PACIENTE);
    }

    @Test(expected = IllegalStateException.class)
    public void anexarExameEmConsulta_paciente_consultaDeOutro_deveRecusar() {
        when(consulta.getStatus()).thenReturn(StatusConsulta.AGENDADA);
        service.anexarExameEmConsulta(1L, PDF_PAYLOAD, "application/pdf", "x.pdf",
                999L, TipoResponsavel.PACIENTE);
    }

    @Test
    public void anexarEmAnotacao_enfermeiro_deveSalvar() {
        ConsultaAnotacao anotacao = mock(ConsultaAnotacao.class);
        when(anotacao.getConsulta()).thenReturn(consulta);
        when(anotacaoDAO.buscarPorId(50L)).thenReturn(anotacao);

        Arquivo a = service.anexarEmAnotacao(50L, PDF_PAYLOAD, "application/pdf", "x.pdf",
                33L, TipoResponsavel.ENFERMEIRO);

        assertEquals(StorageDomain.ANEXO_ANOTACAO, a.getDominio());
        assertEquals(Long.valueOf(50L), a.getIdAnotacao());
    }

    @Test(expected = IllegalStateException.class)
    public void anexarEmAnotacao_medico_consultaDeOutro_deveRecusar() {
        ConsultaAnotacao anotacao = mock(ConsultaAnotacao.class);
        when(anotacao.getConsulta()).thenReturn(consulta);
        when(anotacaoDAO.buscarPorId(50L)).thenReturn(anotacao);

        service.anexarEmAnotacao(50L, PDF_PAYLOAD, "application/pdf", "x.pdf",
                999L, TipoResponsavel.MEDICO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void anexarEmAnotacao_naoEncontrada_deveLancar() {
        when(anotacaoDAO.buscarPorId(99L)).thenReturn(null);
        service.anexarEmAnotacao(99L, PDF_PAYLOAD, "application/pdf", "x.pdf",
                7L, TipoResponsavel.MEDICO);
    }

    @Test
    public void urlDownload_enfermeiro_qualquerArquivo_deveRetornarPresignedUrl() throws Exception {
        Arquivo a = mock(Arquivo.class);
        when(a.getStatus()).thenReturn(IndicativoStatus.A);
        when(a.getPathLogico()).thenReturn("/anexos/consulta/1/2026/06/x.pdf");
        when(arquivoDAO.buscarPorId(11L)).thenReturn(a);
        URL url = new URL("https://signed/x");
        when(storage.presignedGet(eq("/anexos/consulta/1/2026/06/x.pdf"), any(Duration.class)))
                .thenReturn(url);

        assertSame(url, service.urlDownload(11L, 33L, TipoResponsavel.ENFERMEIRO));
    }

    @Test
    public void urlDownload_medico_consultaPropria_deveRetornarUrl() throws Exception {
        Arquivo a = mock(Arquivo.class);
        when(a.getStatus()).thenReturn(IndicativoStatus.A);
        when(a.getPathLogico()).thenReturn("/anexos/consulta/1/2026/06/x.pdf");
        when(a.getIdConsulta()).thenReturn(1L);
        when(arquivoDAO.buscarPorId(11L)).thenReturn(a);
        URL url = new URL("https://signed/x");
        when(storage.presignedGet(anyString(), any(Duration.class))).thenReturn(url);

        assertSame(url, service.urlDownload(11L, 7L, TipoResponsavel.MEDICO));
    }

    @Test(expected = IllegalStateException.class)
    public void urlDownload_medico_consultaDeOutro_deveRecusar() {
        Arquivo a = mock(Arquivo.class);
        when(a.getStatus()).thenReturn(IndicativoStatus.A);
        when(a.getIdConsulta()).thenReturn(1L);
        when(a.getIdAnotacao()).thenReturn(null);
        when(arquivoDAO.buscarPorId(11L)).thenReturn(a);
        service.urlDownload(11L, 999L, TipoResponsavel.MEDICO);
    }

    @Test(expected = IllegalStateException.class)
    public void urlDownload_paciente_consultaDeOutro_deveRecusar() {
        Arquivo a = mock(Arquivo.class);
        when(a.getStatus()).thenReturn(IndicativoStatus.A);
        when(a.getIdConsulta()).thenReturn(1L);
        when(arquivoDAO.buscarPorId(11L)).thenReturn(a);
        service.urlDownload(11L, 999L, TipoResponsavel.PACIENTE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void urlDownload_arquivoInexistente_deveLancar() {
        when(arquivoDAO.buscarPorId(99L)).thenReturn(null);
        service.urlDownload(99L, 1L, TipoResponsavel.ENFERMEIRO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void urlDownload_arquivoInativo_deveLancar() {
        Arquivo a = mock(Arquivo.class);
        when(a.getStatus()).thenReturn(IndicativoStatus.I);
        when(arquivoDAO.buscarPorId(11L)).thenReturn(a);
        service.urlDownload(11L, 1L, TipoResponsavel.ADMIN);
    }

    @Test
    public void remover_autorOriginal_deveInativar() {
        Arquivo a = mock(Arquivo.class);
        when(a.getStatus()).thenReturn(IndicativoStatus.A);
        when(a.getIdResponsavel()).thenReturn(33L);
        when(a.getTipoResponsavel()).thenReturn(TipoResponsavel.ENFERMEIRO);
        when(arquivoDAO.buscarPorId(11L)).thenReturn(a);

        service.remover(11L, 33L, TipoResponsavel.ENFERMEIRO);

        verify(a).inativar();
        verify(arquivoDAO).atualizar(a);
    }

    @Test
    public void remover_admin_qualquerArquivo_deveInativar() {
        Arquivo a = mock(Arquivo.class);
        when(a.getStatus()).thenReturn(IndicativoStatus.A);
        when(a.getIdResponsavel()).thenReturn(99L);
        when(a.getTipoResponsavel()).thenReturn(TipoResponsavel.MEDICO);
        when(arquivoDAO.buscarPorId(11L)).thenReturn(a);

        service.remover(11L, 1L, TipoResponsavel.ADMIN);

        verify(a).inativar();
    }

    @Test(expected = IllegalStateException.class)
    public void remover_terceiro_naoPodeRemover() {
        Arquivo a = mock(Arquivo.class);
        when(a.getStatus()).thenReturn(IndicativoStatus.A);
        when(a.getIdResponsavel()).thenReturn(7L);
        when(a.getTipoResponsavel()).thenReturn(TipoResponsavel.MEDICO);
        when(arquivoDAO.buscarPorId(11L)).thenReturn(a);
        service.remover(11L, 33L, TipoResponsavel.ENFERMEIRO);
    }

    @Test
    public void listarPorConsulta_idNull_retornaListaVazia() {
        assertEquals(0, service.listarPorConsulta(null).size());
    }

    @Test
    public void listarPorAnotacao_idNull_retornaListaVazia() {
        assertEquals(0, service.listarPorAnotacao(null).size());
    }
}
