package br.com.hsg.web.bean.admin;

import br.com.hsg.domain.entity.Especialidade;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.enums.DiaSemana;
import br.com.hsg.domain.vo.Crm;
import br.com.hsg.service.facade.admin.AgendaMedicaServiceFacade;
import br.com.hsg.web.support.FacesContextMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;

import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AgendaMedicaBeanTest {

    @Mock private AgendaMedicaServiceFacade agendaService;
    @InjectMocks private AgendaMedicaBean bean;

    private FacesContext faces;
    private Medico m1;
    private Medico m2;

    @Before
    public void setUp() {
        faces = FacesContextMock.criar();

        m1 = mock(Medico.class);
        when(m1.getId()).thenReturn(1L);
        when(m1.getNomeCompleto()).thenReturn("João Silva");
        when(m1.getCrm()).thenReturn(new Crm("12345", "SP"));
        Especialidade esp = mock(Especialidade.class);
        when(esp.getNome()).thenReturn("Cardiologia");
        when(m1.getEspecialidade()).thenReturn(esp);

        m2 = mock(Medico.class);
        when(m2.getId()).thenReturn(2L);
        when(m2.getNomeCompleto()).thenReturn("Ana Souza");

        when(agendaService.listarMedicosAtivos()).thenReturn(Arrays.asList(m1, m2));
        when(agendaService.listarGradesPorMedico(anyLong())).thenReturn(Collections.emptyList());
        when(agendaService.listarExcecoesPorMedico(anyLong())).thenReturn(Collections.emptyList());
        when(agendaService.listarEspecialidadesDoMedico(anyLong())).thenReturn(Collections.emptyList());
        when(agendaService.listarSlotsPorMedicoPeriodo(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(agendaService.listarSlotsPorMedicosPeriodo(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(agendaService.listarExcecoesPorMedicosVigentes(any(), any(), any()))
                .thenReturn(Collections.emptyList());

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
    public void init_deveSelecionarPrimeiroMedicoEPopularCalendarioMulti() {
        assertEquals(Long.valueOf(1L), bean.getMedicoSelecionadoId());
        assertTrue(bean.getIdsMedicosCalendario().contains(1L));
    }

    @Test
    public void aoMudarMedico_deveAdicionarIdAoCalendarioMultiSeAusente() {
        bean.setMedicoSelecionadoId(2L);

        bean.aoMudarMedico();

        assertTrue(bean.getIdsMedicosCalendario().contains(2L));
    }

    @Test
    public void selecionarTodosCalendarioMulti_devePreencherComTodosMedicos() {
        bean.limparCalendarioMulti();
        bean.selecionarTodosCalendarioMulti();

        assertEquals(2, bean.getIdsMedicosCalendario().size());
    }

    @Test
    public void selecionarTodosMedicosBulk_devePopular_LimparEsvazia() {
        bean.selecionarTodosMedicosBulk();
        assertEquals(2, bean.getIdsBulkSelecionados().size());

        bean.limparSelecaoBulk();
        assertTrue(bean.getIdsBulkSelecionados().isEmpty());
    }

    @Test
    public void labelMedicoDropdown_deveIncluirCrmEEspecialidade() {
        String label = bean.labelMedicoDropdown(m1);
        assertTrue(label.contains("João Silva"));
        assertTrue(label.contains("CRM-SP 12345"));
        assertTrue(label.contains("Cardiologia"));
    }

    @Test
    public void salvarGrade_deveBloquearSemMedicoSelecionado() {
        bean.setMedicoSelecionadoId(null);

        bean.salvarGrade();

        assertEquals(FacesMessage.SEVERITY_WARN, ultimaMensagem().getSeverity());
        verify(agendaService, never()).criarGrade(any(), any(), any(), any(), any());
    }

    @Test
    public void salvarGrade_deveBloquearSemHorarios() {
        bean.setFormGradeHoraInicio(null);
        bean.setFormGradeHoraFim(null);
        bean.setFormGradeDiaSemana(DiaSemana.SEGUNDA.name());

        bean.salvarGrade();

        assertEquals(FacesMessage.SEVERITY_WARN, ultimaMensagem().getSeverity());
        verify(agendaService, never()).criarGrade(any(), any(), any(), any(), any());
    }

    @Test
    public void salvarGrade_deveBloquearSemDiaSemana() {
        bean.setFormGradeDiaSemana(null);

        bean.salvarGrade();

        assertEquals(FacesMessage.SEVERITY_WARN, ultimaMensagem().getSeverity());
        verify(agendaService, never()).criarGrade(any(), any(), any(), any(), any());
    }

    @Test
    public void salvarGrade_deveDelegarCriarQuandoValido() {
        bean.setFormGradeDiaSemana(DiaSemana.SEGUNDA.name());

        bean.salvarGrade();

        verify(agendaService).criarGrade(eq(1L), eq(DiaSemana.SEGUNDA), any(), any(), eq(30));
    }

    @Test
    public void salvarGrade_deveExibirMensagemDeExceptionEnvolvidaPorEjb() {
        bean.setFormGradeDiaSemana(DiaSemana.SEGUNDA.name());
        IllegalStateException causa = new IllegalStateException("Já existe grade ativa neste dia/horário.");
        doThrow(new EJBException(causa))
                .when(agendaService).criarGrade(any(), any(), any(), any(), any());

        bean.salvarGrade();

        FacesMessage msg = ultimaMensagem();
        assertEquals(FacesMessage.SEVERITY_ERROR, msg.getSeverity());
        assertEquals("Já existe grade ativa neste dia/horário.", msg.getSummary());
    }

    @Test
    public void salvarExcecao_deveBloquearSemDatas() {
        bean.setFormExcInicio(null);
        bean.setFormExcFim(null);

        bean.salvarExcecao();

        assertEquals(FacesMessage.SEVERITY_WARN, ultimaMensagem().getSeverity());
        verify(agendaService, never()).criarExcecao(any(), any(), any(), any(), any());
    }

    @Test
    public void salvarExcecao_deveBloquearQuandoFimNaoMaiorQueInicio() {
        Date d = new Date();
        bean.setFormExcInicio(d);
        bean.setFormExcFim(d);

        bean.salvarExcecao();

        assertEquals(FacesMessage.SEVERITY_WARN, ultimaMensagem().getSeverity());
        verify(agendaService, never()).criarExcecao(any(), any(), any(), any(), any());
    }

    @Test
    public void gerarSlots_deveBloquearSemMedico() {
        bean.setMedicoSelecionadoId(null);

        bean.gerarSlots();

        assertEquals(FacesMessage.SEVERITY_WARN, ultimaMensagem().getSeverity());
        verify(agendaService, never()).gerarSlots(any(), anyInt());
    }

    @Test
    public void gerarSlots_deveBloquearDiasForaDoRange() {
        bean.setDiasGerar(0);

        bean.gerarSlots();

        assertEquals(FacesMessage.SEVERITY_WARN, ultimaMensagem().getSeverity());
        verify(agendaService, never()).gerarSlots(any(), anyInt());
    }

    @Test
    public void gerarSlotsEmMassa_deveBloquearSelecaoVazia() {
        bean.limparSelecaoBulk();

        bean.gerarSlotsEmMassa();

        assertEquals(FacesMessage.SEVERITY_WARN, ultimaMensagem().getSeverity());
        verify(agendaService, never()).gerarSlots(any(), anyInt());
    }

    @Test
    public void gerarSlotsEmMassa_deveIterarSobreSelecaoEAgregarTotal() {
        bean.selecionarTodosMedicosBulk();
        when(agendaService.gerarSlots(1L, 30)).thenReturn(5);
        when(agendaService.gerarSlots(2L, 30)).thenReturn(3);

        bean.gerarSlotsEmMassa();

        verify(agendaService).gerarSlots(1L, 30);
        verify(agendaService).gerarSlots(2L, 30);
        FacesMessage msg = ultimaMensagem();
        assertEquals(FacesMessage.SEVERITY_INFO, msg.getSeverity());
        assertTrue(msg.getSummary().contains("8 slot(s)"));
        assertTrue(msg.getSummary().contains("2 médico(s)"));
    }

    @Test
    public void gerarSlotsEmMassa_deveReportarFalhasComoWarn() {
        bean.selecionarTodosMedicosBulk();
        when(agendaService.gerarSlots(1L, 30)).thenReturn(4);
        when(agendaService.gerarSlots(2L, 30)).thenThrow(new EJBException("boom"));

        bean.gerarSlotsEmMassa();

        FacesMessage msg = ultimaMensagem();
        assertEquals(FacesMessage.SEVERITY_WARN, msg.getSeverity());
        assertTrue(msg.getSummary().contains("1 médico(s) falharam"));
    }

    @Test
    public void prepararEdicaoGrade_deveAbrirAccordionESalvarId() {
        br.com.hsg.domain.entity.AgendaMedica grade = mock(br.com.hsg.domain.entity.AgendaMedica.class);
        when(grade.getId()).thenReturn(77L);
        when(grade.getDiaSemana()).thenReturn(DiaSemana.QUARTA);
        when(grade.getHoraInicio()).thenReturn(java.time.LocalTime.of(14, 0));
        when(grade.getHoraFim()).thenReturn(java.time.LocalTime.of(18, 0));
        when(grade.getDuracaoMinutos()).thenReturn(45);

        bean.prepararEdicaoGrade(grade);

        assertEquals(Long.valueOf(77L), bean.getFormGradeId());
        assertEquals("0", bean.getAccordionFormIndex());
    }

    @Test
    public void novaGrade_deveFecharAccordionELimparId() {
        bean.setFormGradeId(7L);
        bean.setAccordionFormIndex("0");

        bean.novaGrade();

        assertNull(bean.getFormGradeId());
        assertEquals("-1", bean.getAccordionFormIndex());
    }

    @Test
    public void onEventSelect_deveParsearDescricaoDeSlot() {
        DefaultScheduleEvent ev = new DefaultScheduleEvent();
        ev.setDescription("SLOT|João Silva|Livre|01/06/2026 08:00|01/06/2026 08:30|Cardiologia|evt-livre");
        SelectEvent se = mock(SelectEvent.class);
        when(se.getObject()).thenReturn((ScheduleEvent) ev);

        bean.onEventSelect(se);

        assertEquals("SLOT", bean.getDetalheTipo());
        assertEquals("João Silva", bean.getDetalheMedicoNome());
        assertEquals("Livre", bean.getDetalheStatus());
        assertEquals("Cardiologia", bean.getDetalheEspecialidade());
        assertNull(bean.getDetalheMotivo());
        assertEquals("evt-livre", bean.getDetalheCorClasse());
        assertEquals("Horário (slot)", bean.getDetalheTitulo());
    }

    @Test
    public void onEventSelect_deveParsearDescricaoDeExcecao() {
        DefaultScheduleEvent ev = new DefaultScheduleEvent();
        ev.setDescription("EXCECAO|Ana Souza|Férias|01/06/2026 00:00|08/06/2026 00:00|Recesso|evt-excecao");
        SelectEvent se = mock(SelectEvent.class);
        when(se.getObject()).thenReturn((ScheduleEvent) ev);

        bean.onEventSelect(se);

        assertEquals("EXCECAO", bean.getDetalheTipo());
        assertEquals("Recesso", bean.getDetalheMotivo());
        assertNull(bean.getDetalheEspecialidade());
        assertEquals("Exceção da agenda", bean.getDetalheTitulo());
    }
}
