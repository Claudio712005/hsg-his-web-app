package br.com.hsg.web.bean.admin;

import br.com.hsg.domain.entity.SolicitacaoConvenio;
import br.com.hsg.service.facade.admin.AprovacaoConvenioServiceFacade;
import br.com.hsg.web.bean.session.BeanSessao;
import br.com.hsg.web.dto.response.AdminResponseDTO;
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
public class AprovacaoConvenioBeanTest {

    @Mock private AprovacaoConvenioServiceFacade aprovacaoService;
    @Mock private BeanSessao beanSessao;
    @InjectMocks private AprovacaoConvenioBean bean;

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

    private SolicitacaoConvenio selecionada(Long id) {
        SolicitacaoConvenio s = mock(SolicitacaoConvenio.class);
        when(s.getId()).thenReturn(id);
        bean.setSolicitacaoSelecionada(s);
        return s;
    }

    @Test
    public void aprovar_deveChamarServicoComIdDoAdminLogado() {
        AdminResponseDTO admin = mock(AdminResponseDTO.class);
        when(admin.getId()).thenReturn(9L);
        when(beanSessao.getAdmin()).thenReturn(admin);
        selecionada(1L);

        bean.aprovar();

        verify(aprovacaoService).aprovar(1L, 9L);
        assertEquals(FacesMessage.SEVERITY_INFO, capturarMensagem().getSeverity());
    }

    @Test
    public void aprovar_naoDeveFazerNadaSemSelecao() {
        bean.aprovar();
        verify(aprovacaoService, never()).aprovar(anyLong(), any());
        verify(faces, never()).addMessage(any(), any());
    }

    @Test
    public void aprovar_deveExibirWarnQuandoServicoFalha() {
        selecionada(1L);
        doThrow(new IllegalStateException("Solicitação não encontrada"))
                .when(aprovacaoService).aprovar(anyLong(), any());

        bean.aprovar();

        assertEquals(FacesMessage.SEVERITY_WARN, capturarMensagem().getSeverity());
    }

    @Test
    public void rejeitar_deveExigirMotivo() {
        selecionada(1L);
        bean.setMotivoRejeicao("  ");

        bean.rejeitar();

        verify(aprovacaoService, never()).rejeitar(anyLong(), any(), any());
        assertEquals(FacesMessage.SEVERITY_WARN, capturarMensagem().getSeverity());
    }

    @Test
    public void rejeitar_deveChamarServicoQuandoMotivoInformado() {
        AdminResponseDTO admin = mock(AdminResponseDTO.class);
        when(admin.getId()).thenReturn(9L);
        when(beanSessao.getAdmin()).thenReturn(admin);
        selecionada(1L);
        bean.setMotivoRejeicao("Documentação inválida");

        bean.rejeitar();

        verify(aprovacaoService).rejeitar(1L, 9L, "Documentação inválida");
        assertEquals(FacesMessage.SEVERITY_INFO, capturarMensagem().getSeverity());
    }
}
