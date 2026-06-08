package br.com.hsg.web.bean.paciente;

import br.com.hsg.domain.entity.AgendaMedicaSlot;
import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.service.facade.paciente.ConsultaBuscaServiceFacade;
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
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConsultaBuscaBeanTest {

    @Mock private BeanSessao beanSessao;
    @Mock private ConsultaBuscaServiceFacade buscaService;
    @Mock private ConsultaServiceFacade consultaService;

    @InjectMocks private ConsultaBuscaBean bean;

    private FacesContext faces;

    @Before
    public void setUp() {
        faces = FacesContextMock.criar();
        PacienteResponseDTO pac = mock(PacienteResponseDTO.class);
        when(pac.getId()).thenReturn(10L);
        when(beanSessao.getPaciente()).thenReturn(pac);
        when(buscaService.listarEspecialidadesAtivas()).thenReturn(Collections.emptyList());
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
    public void buscarHorarios_deveBloquearSemEspecialidade() {
        bean.setEspecialidadeSelecionadaId(null);

        bean.buscarHorarios();

        assertEquals(FacesMessage.SEVERITY_WARN, ultimaMensagem().getSeverity());
        verify(buscaService, never()).listarHorariosLivresProximos(any(), anyInt(), any());
    }

    @Test
    public void buscarHorarios_deveDelegarQuandoValido() {
        bean.setEspecialidadeSelecionadaId(1L);
        when(buscaService.listarHorariosLivresProximos(eq(1L), anyInt(), isNull()))
                .thenReturn(Collections.emptyList());

        bean.buscarHorarios();

        verify(buscaService).listarHorariosLivresProximos(eq(1L), anyInt(), isNull());
    }

    @Test
    public void prepararAgendamento_deveSimularFinanceiro() {
        AgendaMedicaSlot slot = mock(AgendaMedicaSlot.class);
        when(slot.getId()).thenReturn(55L);

        bean.prepararAgendamento(slot);

        verify(consultaService).simular(eq(10L), eq(55L), anyBoolean());
    }

    @Test
    public void confirmarAgendamento_deveBloquearSemSlot() {
        bean.confirmarAgendamento();

        assertEquals(FacesMessage.SEVERITY_WARN, ultimaMensagem().getSeverity());
        verify(consultaService, never()).agendar(any(), any(), any(), anyBoolean());
    }

    @Test
    public void aoMudarEspecialidade_semEspecialidade_deveLimparMedicos() {
        bean.setEspecialidadeSelecionadaId(null);

        bean.aoMudarEspecialidade();

        assertEquals(0, bean.getMedicos().size());
        assertFalse(bean.isBuscou());
    }

    @Test
    public void aoMudarEspecialidade_comEspecialidade_deveCarregarMedicosEHorarios() {
        bean.setEspecialidadeSelecionadaId(1L);
        when(buscaService.listarMedicosPorEspecialidade(1L))
                .thenReturn(Collections.emptyList());
        when(buscaService.listarHorariosLivresProximos(eq(1L), anyInt(), any()))
                .thenReturn(Collections.emptyList());

        bean.aoMudarEspecialidade();

        verify(buscaService).listarMedicosPorEspecialidade(1L);
        verify(buscaService).listarHorariosLivresProximos(eq(1L), anyInt(), any());
    }

    @Test
    public void recalcularFinanceiro_semSlot_naoChamaServico() {
        bean.recalcularFinanceiro();

        verify(consultaService, never()).simular(any(), any(), anyBoolean());
    }

    @Test
    public void confirmarAgendamento_deveDelegarAoServico() {
        AgendaMedicaSlot slot = mock(AgendaMedicaSlot.class);
        when(slot.getId()).thenReturn(55L);
        bean.setEspecialidadeSelecionadaId(1L);
        bean.prepararAgendamento(slot);

        Consulta c = mock(Consulta.class);
        when(c.getDataConsulta()).thenReturn(LocalDateTime.now().plusDays(2));
        when(consultaService.agendar(eq(10L), eq(1L), eq(55L), anyBoolean())).thenReturn(c);
        when(buscaService.listarHorariosLivresProximos(any(), anyInt(), any())).thenReturn(Collections.emptyList());

        bean.confirmarAgendamento();

        verify(consultaService).agendar(eq(10L), eq(1L), eq(55L), anyBoolean());
    }
}
