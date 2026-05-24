package br.com.hsg.web.bean.public_;

import br.com.hsg.domain.entity.PreCadastroProfissional;
import br.com.hsg.service.facade.public_.AtivacaoServiceFacade;
import br.com.hsg.web.support.FacesContextMock;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AtivacaoProfissionalBeanTest {

    @Mock private AtivacaoServiceFacade ativacaoService;
    @InjectMocks private AtivacaoProfissionalBean bean;

    @After
    public void tearDown() {
        FacesContextMock.liberar();
    }

    @Test
    public void init_deveDefinirErroQuandoTokenVazio() {
        bean.setToken("  ");
        bean.init();
        assertNotNull(bean.getErroToken());
        assertFalse(bean.isPreRenderado());
    }

    @Test
    public void init_deveDefinirErroQuandoTokenNaoEncontrado() {
        bean.setToken("tok");
        when(ativacaoService.validarToken("tok")).thenReturn(null);
        bean.init();
        assertNotNull(bean.getErroToken());
    }

    @Test
    public void init_deveDefinirErroQuandoConvitejaUtilizado() {
        PreCadastroProfissional pre = mock(PreCadastroProfissional.class);
        when(pre.isPendente()).thenReturn(false);
        bean.setToken("tok");
        when(ativacaoService.validarToken("tok")).thenReturn(pre);
        bean.init();
        assertNotNull(bean.getErroToken());
    }

    @Test
    public void init_deveRenderarFormularioQuandoConviteValido() {
        PreCadastroProfissional pre = mock(PreCadastroProfissional.class);
        when(pre.isPendente()).thenReturn(true);
        when(pre.isConviteExpirado()).thenReturn(false);
        when(pre.isMedico()).thenReturn(false);
        bean.setToken("tok");
        when(ativacaoService.validarToken("tok")).thenReturn(pre);

        bean.init();

        assertNull(bean.getErroToken());
        assertTrue(bean.isPreRenderado());
        assertSame(pre, bean.getPreCadastro());
    }

    @Test
    public void confirmar_deveBloquearQuandoSenhasNaoConferem() {
        FacesContext faces = FacesContextMock.criar();
        bean.setToken("tok");
        bean.getForm().setSenha("Senha@123");
        bean.getForm().setConfirmacaoSenha("Outra@123");
        bean.verificarSenhas();

        bean.confirmar();

        verify(ativacaoService, never()).ativarCadastro(any(), any());
        ArgumentCaptor<FacesMessage> captor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(faces).addMessage(isNull(String.class), captor.capture());
        assertEquals(FacesMessage.SEVERITY_ERROR, captor.getValue().getSeverity());
    }

    @Test
    public void confirmar_deveExibirErroDeNegocio() {
        FacesContext faces = FacesContextMock.criar();
        bean.setToken("tok");
        doThrow(new IllegalStateException("Convite expirado"))
                .when(ativacaoService).ativarCadastro(eq("tok"), any());

        bean.confirmar();

        assertFalse(bean.isConcluido());
        ArgumentCaptor<FacesMessage> captor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(faces).addMessage(isNull(String.class), captor.capture());
        assertEquals("Convite expirado", captor.getValue().getSummary());
    }
}
