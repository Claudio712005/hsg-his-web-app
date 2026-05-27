package br.com.hsg.service.impl.admin;

import br.com.hsg.dao.AgendaMedicaDAO;
import br.com.hsg.dao.AgendaMedicaExcecaoDAO;
import br.com.hsg.dao.AgendaMedicaSlotDAO;
import br.com.hsg.dao.MedicoDAO;
import br.com.hsg.domain.entity.AgendaMedica;
import br.com.hsg.domain.entity.AgendaMedicaExcecao;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.enums.DiaSemana;
import br.com.hsg.domain.enums.TipoExcecaoAgenda;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AgendaMedicaServiceImplTest {

    @Mock private MedicoDAO medicoDAO;
    @Mock private AgendaMedicaDAO agendaMedicaDAO;
    @Mock private AgendaMedicaExcecaoDAO agendaMedicaExcecaoDAO;
    @Mock private AgendaMedicaSlotDAO agendaMedicaSlotDAO;

    @InjectMocks private AgendaMedicaServiceImpl service;

    private Medico medico;

    @Before
    public void setUp() {
        medico = mock(Medico.class);
        when(medicoDAO.buscarPorId(1L)).thenReturn(medico);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criarGrade_deveLancarQuandoMedicoNaoEncontrado() {
        when(medicoDAO.buscarPorId(99L)).thenReturn(null);
        service.criarGrade(99L, DiaSemana.SEGUNDA,
                LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
    }

    @Test(expected = IllegalStateException.class)
    public void criarGrade_deveLancarQuandoSobreposta() {
        when(agendaMedicaDAO.buscarSobreposicao(
                eq(1L), eq(DiaSemana.SEGUNDA), any(), any(), eq((Long) null)))
                .thenReturn(Collections.singletonList(mock(AgendaMedica.class)));

        service.criarGrade(1L, DiaSemana.SEGUNDA,
                LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
    }

    @Test
    public void criarGrade_deveSalvarQuandoSemSobreposicao() {
        when(agendaMedicaDAO.buscarSobreposicao(
                eq(1L), eq(DiaSemana.SEGUNDA), any(), any(), eq((Long) null)))
                .thenReturn(Collections.emptyList());
        AgendaMedica salva = mock(AgendaMedica.class);
        when(salva.getId()).thenReturn(10L);
        when(agendaMedicaDAO.salvar(any(AgendaMedica.class))).thenReturn(salva);

        service.criarGrade(1L, DiaSemana.SEGUNDA,
                LocalTime.of(8, 0), LocalTime.of(12, 0), 30);

        verify(agendaMedicaDAO).salvar(any(AgendaMedica.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void atualizarGrade_deveLancarQuandoGradeNaoEncontrada() {
        when(agendaMedicaDAO.buscarPorId(7L)).thenReturn(null);
        service.atualizarGrade(7L, LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
    }

    @Test
    public void atualizarGrade_devePassarIdDaProprioGradeNoExcluir() {
        AgendaMedica grade = mock(AgendaMedica.class);
        when(grade.getMedico()).thenReturn(medico);
        when(medico.getId()).thenReturn(1L);
        when(grade.getDiaSemana()).thenReturn(DiaSemana.SEGUNDA);
        when(agendaMedicaDAO.buscarPorId(7L)).thenReturn(grade);
        when(agendaMedicaDAO.buscarSobreposicao(
                eq(1L), eq(DiaSemana.SEGUNDA), any(), any(), eq(7L)))
                .thenReturn(Collections.emptyList());

        service.atualizarGrade(7L, LocalTime.of(9, 0), LocalTime.of(13, 0), 60);

        verify(grade).atualizar(LocalTime.of(9, 0), LocalTime.of(13, 0), 60);
        verify(agendaMedicaDAO).buscarSobreposicao(
                eq(1L), eq(DiaSemana.SEGUNDA), any(), any(), eq(7L));
    }

    @Test(expected = IllegalStateException.class)
    public void criarExcecao_deveLancarQuandoSobreposta() {
        LocalDateTime ini = LocalDateTime.of(2026, 6, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 6, 8, 0, 0);
        AgendaMedicaExcecao conflito = stubExcecao();
        when(agendaMedicaExcecaoDAO.buscarSobreposicao(
                eq(1L), eq(ini), eq(fim), eq((Long) null)))
                .thenReturn(Collections.singletonList(conflito));

        service.criarExcecao(1L, ini, fim, TipoExcecaoAgenda.FERIAS, null);
    }

    @Test
    public void criarExcecao_deveSalvarQuandoSemSobreposicao() {
        LocalDateTime ini = LocalDateTime.of(2026, 6, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 6, 8, 0, 0);
        when(agendaMedicaExcecaoDAO.buscarSobreposicao(
                eq(1L), eq(ini), eq(fim), eq((Long) null)))
                .thenReturn(Collections.emptyList());
        AgendaMedicaExcecao salva = mock(AgendaMedicaExcecao.class);
        when(salva.getId()).thenReturn(20L);
        when(agendaMedicaExcecaoDAO.salvar(any(AgendaMedicaExcecao.class))).thenReturn(salva);

        service.criarExcecao(1L, ini, fim, TipoExcecaoAgenda.FERIAS, "Férias");

        verify(agendaMedicaExcecaoDAO).salvar(any(AgendaMedicaExcecao.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void gerarSlots_deveLancarQuandoDiasForaDoRange() {
        service.gerarSlots(1L, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void gerarSlots_deveLancarQuandoMedicoSemGradeAtiva() {
        when(agendaMedicaDAO.listarAtivasPorMedico(1L)).thenReturn(Collections.emptyList());
        service.gerarSlots(1L, 30);
    }

    @Test
    public void gerarSlots_deveSerIdempotentePulandoSlotJaExistente() {
        AgendaMedica grade = stubGrade(LocalTime.of(8, 0), LocalTime.of(9, 0), 30);
        when(agendaMedicaDAO.listarAtivasPorMedico(1L)).thenReturn(Collections.singletonList(grade));
        when(agendaMedicaExcecaoDAO.listarVigentesNoPeriodo(eq(1L), any(), any()))
                .thenReturn(Collections.emptyList());
        when(agendaMedicaSlotDAO.existePorMedicoDataInicio(eq(1L), any())).thenReturn(true);

        int criados = service.gerarSlots(1L, 30);

        assertEquals(0, criados);
        verify(agendaMedicaSlotDAO, never()).salvar(any());
    }

    @Test
    public void gerarSlots_devePularSlotDentroDeExcecao() {
        AgendaMedica grade = stubGrade(LocalTime.of(8, 0), LocalTime.of(9, 0), 30);
        when(agendaMedicaDAO.listarAtivasPorMedico(1L)).thenReturn(Collections.singletonList(grade));

        AgendaMedicaExcecao exc = mock(AgendaMedicaExcecao.class);
        when(exc.getDataInicio()).thenReturn(LocalDateTime.now().minusDays(1));
        when(exc.getDataFim()).thenReturn(LocalDateTime.now().plusDays(365));
        when(agendaMedicaExcecaoDAO.listarVigentesNoPeriodo(eq(1L), any(), any()))
                .thenReturn(Arrays.asList(exc));
        when(agendaMedicaSlotDAO.existePorMedicoDataInicio(eq(1L), any())).thenReturn(false);

        int criados = service.gerarSlots(1L, 30);

        assertEquals(0, criados);
        verify(agendaMedicaSlotDAO, never()).salvar(any());
    }

    private AgendaMedica stubGrade(LocalTime ini, LocalTime fim, int duracaoMin) {
        AgendaMedica g = mock(AgendaMedica.class);
        when(g.getDiaSemana()).thenAnswer(inv -> DiaSemana.fromDayOfWeek(java.time.LocalDate.now().getDayOfWeek()));
        when(g.getHoraInicio()).thenReturn(ini);
        when(g.getHoraFim()).thenReturn(fim);
        when(g.getDuracaoMinutos()).thenReturn(duracaoMin);
        return g;
    }

    private AgendaMedicaExcecao stubExcecao() {
        AgendaMedicaExcecao e = mock(AgendaMedicaExcecao.class);
        when(e.getTipo()).thenReturn(TipoExcecaoAgenda.FERIAS);
        when(e.getDataInicio()).thenReturn(LocalDateTime.now());
        when(e.getDataFim()).thenReturn(LocalDateTime.now().plusDays(7));
        return e;
    }
}
