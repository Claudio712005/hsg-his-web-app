package br.com.hsg.web.bean.paciente;

import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.enums.StatusConsulta;
import br.com.hsg.service.facade.paciente.ConsultaServiceFacade;
import br.com.hsg.web.bean.session.BeanSessao;
import br.com.hsg.web.dto.response.PacienteResponseDTO;
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
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MinhasConsultasBeanTest {

    @Mock private BeanSessao beanSessao;
    @Mock private ConsultaServiceFacade consultaService;

    @InjectMocks private MinhasConsultasBean bean;

    private FacesContext faces;

    @Before
    public void setUp() {
        faces = FacesContextMock.criar();
        PacienteResponseDTO pac = mock(PacienteResponseDTO.class);
        when(pac.getId()).thenReturn(10L);
        when(beanSessao.getPaciente()).thenReturn(pac);
        when(consultaService.listarConsultasPaciente(10L)).thenReturn(Collections.emptyList());
        bean.init();
    }

    @After
    public void tearDown() {
        FacesContextMock.liberar();
    }

    private FacesMessage ultimaMensagem() {
        ArgumentCaptor<FacesMessage> captor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(faces, atLeastOnce()).addMessage(isNull(String.class), captor.capture());
        return captor.getValue();
    }

    @Test
    public void cancelar_deveBloquearSemConsultaSelecionada() {
        bean.cancelar();

        assertEquals(FacesMessage.SEVERITY_WARN, ultimaMensagem().getSeverity());
        verify(consultaService, never()).cancelarPeloPaciente(any(), any(), any());
    }

    @Test
    public void cancelar_deveBloquearSemMotivo() {
        bean.prepararCancelamento(5L);

        bean.cancelar();

        assertEquals(FacesMessage.SEVERITY_WARN, ultimaMensagem().getSeverity());
        verify(consultaService, never()).cancelarPeloPaciente(any(), any(), any());
    }

    @Test
    public void cancelar_deveDelegarQuandoValido() {
        bean.prepararCancelamento(5L);
        bean.setMotivoCancelamento("imprevisto");

        bean.cancelar();

        verify(consultaService).cancelarPeloPaciente(eq(5L), eq(10L), eq("imprevisto"));
    }

    @Test
    public void verDetalhes_deveArmazenarConsultaSelecionada() {
        Consulta c = mock(Consulta.class);
        bean.verDetalhes(c);
        org.junit.Assert.assertSame(c, bean.getConsultaSelecionada());
    }

    @Test
    public void chipStatus_deveMapearTodosOsStatus() {
        Consulta c = mock(Consulta.class);

        when(c.getStatus()).thenReturn(StatusConsulta.AGENDADA);
        org.junit.Assert.assertEquals("pendente", bean.chipStatus(c));

        when(c.getStatus()).thenReturn(StatusConsulta.CONFIRMADA);
        org.junit.Assert.assertEquals("ativo", bean.chipStatus(c));

        when(c.getStatus()).thenReturn(StatusConsulta.REALIZADA);
        org.junit.Assert.assertEquals("ativo", bean.chipStatus(c));

        when(c.getStatus()).thenReturn(StatusConsulta.CANCELADA);
        org.junit.Assert.assertEquals("inativo", bean.chipStatus(c));

        when(c.getStatus()).thenReturn(StatusConsulta.FALTOU);
        org.junit.Assert.assertEquals("inativo", bean.chipStatus(c));
    }
}
