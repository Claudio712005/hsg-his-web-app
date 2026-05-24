package br.com.hsg.web.bean.admin;

import br.com.hsg.service.facade.admin.ConvenioServiceFacade;
import br.com.hsg.web.bean.session.BeanSessao;
import br.com.hsg.web.support.FacesContextMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConvenioBeanTest {

    @Mock private ConvenioServiceFacade convenioService;
    @Mock private BeanSessao beanSessao;
    @InjectMocks private ConvenioBean bean;

    private FacesContext faces;

    @Before
    public void setUp() {
        faces = FacesContextMock.criar();
        bean.init();
    }

    @After
    public void tearDown() {
        FacesContextMock.liberar();
    }

    private FacesMessage capturarMensagem() {
        ArgumentCaptor<FacesMessage> captor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(faces, atLeastOnce()).addMessage(isNull(String.class), captor.capture());
        return captor.getValue();
    }

    @Test
    public void salvarConvenio_deveCriarQuandoIdNulo() {
        bean.setFormConvenioNome("Unimed");

        bean.salvarConvenio();

        verify(convenioService).criarConvenio("Unimed", null, null, null, null, null);
        assertEquals(FacesMessage.SEVERITY_INFO, capturarMensagem().getSeverity());
    }

    @Test
    public void salvarConvenio_deveAtualizarQuandoIdPreenchido() {
        bean.setFormConvenioId(5L);
        bean.setFormConvenioNome("Bradesco");

        bean.salvarConvenio();

        verify(convenioService).atualizarConvenio(eq(5L), eq("Bradesco"), any(), any(), any(), any(), any());
        verify(convenioService, never()).criarConvenio(any(), any(), any(), any(), any(), any());
    }

    @Test
    public void salvarConvenio_deveExibirErroQuandoServicoFalha() {
        bean.setFormConvenioNome("Unimed");
        doThrow(new IllegalStateException("Já existe")).when(convenioService)
                .criarConvenio(any(), any(), any(), any(), any(), any());

        bean.salvarConvenio();

        FacesMessage msg = capturarMensagem();
        assertEquals(FacesMessage.SEVERITY_ERROR, msg.getSeverity());
        assertEquals("Já existe", msg.getSummary());
    }

    @Test
    public void inativarConvenio_deveExibirErroQuandoServicoFalha() {
        doThrow(new IllegalStateException("Possui planos ativos")).when(convenioService).inativarConvenio(1L);

        bean.inativarConvenio(1L);

        FacesMessage msg = capturarMensagem();
        assertEquals(FacesMessage.SEVERITY_ERROR, msg.getSeverity());
        assertEquals("Possui planos ativos", msg.getSummary());
    }

    @Test
    public void ativarConvenio_deveExibirInfoQuandoSucesso() {
        bean.ativarConvenio(1L);

        verify(convenioService).ativarConvenio(1L);
        assertEquals(FacesMessage.SEVERITY_INFO, capturarMensagem().getSeverity());
    }
}
