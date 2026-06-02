package br.com.hsg.service.impl.clinica;

import br.com.hsg.dao.AgendaMedicaSlotDAO;
import br.com.hsg.dao.ConsultaDAO;
import br.com.hsg.dao.ConsultaAnotacaoDAO;
import br.com.hsg.dao.ConsultaHistoricoDAO;
import br.com.hsg.domain.entity.ConsultaAnotacao;
import br.com.hsg.domain.entity.ConsultaHistorico;
import br.com.hsg.domain.entity.AgendaMedicaSlot;
import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.domain.enums.StatusConsulta;
import br.com.hsg.domain.enums.TipoResponsavel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConsultaClinicaServiceImplTest {

    @Mock private ConsultaDAO consultaDAO;
    @Mock private ConsultaHistoricoDAO historicoDAO;
    @Mock private ConsultaAnotacaoDAO anotacaoDAO;
    @Mock private AgendaMedicaSlotDAO agendaMedicaSlotDAO;

    @InjectMocks private ConsultaClinicaServiceImpl service;

    private Consulta consulta;
    private Medico medico;
    private Paciente paciente;
    private AgendaMedicaSlot slot;

    @Before
    public void setUp() {
        medico = mock(Medico.class);
        when(medico.getId()).thenReturn(7L);
        paciente = mock(Paciente.class);
        slot = mock(AgendaMedicaSlot.class);
        when(slot.getId()).thenReturn(55L);

        consulta = mock(Consulta.class);
        when(consulta.getMedico()).thenReturn(medico);
        when(consulta.getPaciente()).thenReturn(paciente);
        when(consulta.getSlot()).thenReturn(slot);
        when(consulta.getDataConsulta()).thenReturn(LocalDateTime.now().plusHours(2));
        when(consultaDAO.buscarPorIdComMedico(1L)).thenReturn(consulta);
    }

    @Test(expected = IllegalArgumentException.class)
    public void listarConsultasDoDia_deveLancarSemData() {
        service.listarConsultasDoDia(null, null);
    }

    @Test
    public void listarConsultasDoDia_deveDelegarAoDao() {
        LocalDate dia = LocalDate.of(2026, 6, 1);
        service.listarConsultasDoDia(dia, 5L);
        verify(consultaDAO).listarDoDia(eq(dia.atStartOfDay()),
                eq(dia.plusDays(1).atStartOfDay()), eq(5L));
    }

    @Test
    public void confirmarChegada_deveConfirmarEPersist() {
        when(consulta.getStatus()).thenReturn(StatusConsulta.AGENDADA);

        service.confirmarChegada(1L, 99L, TipoResponsavel.ENFERMEIRO);

        verify(consulta).confirmar();
        verify(consultaDAO).atualizar(consulta);
    }

    @Test(expected = IllegalStateException.class)
    public void confirmarChegada_deveLancarSeNaoAgendada() {
        when(consulta.getStatus()).thenReturn(StatusConsulta.CONFIRMADA);
        service.confirmarChegada(1L, 99L, TipoResponsavel.ENFERMEIRO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void confirmarChegada_deveLancarQuandoConsultaNaoEncontrada() {
        when(consultaDAO.buscarPorIdComMedico(99L)).thenReturn(null);
        service.confirmarChegada(99L, 1L, TipoResponsavel.ENFERMEIRO);
    }

    @Test(expected = IllegalStateException.class)
    public void marcarRealizada_deveLancarSeMedicoNaoForResponsavel() {
        service.marcarRealizadaComObservacao(1L, 999L, "obs");
    }

    @Test
    public void marcarRealizada_delegaParaEntidadeEPersist() {
        service.marcarRealizadaComObservacao(1L, 7L, "Paciente OK");

        verify(consulta).marcarRealizadaComObservacao("Paciente OK");
        verify(consultaDAO).atualizar(consulta);
    }

    @Test
    public void marcarFaltaPelaClinica_deveDelegar() {
        service.marcarFaltaPelaClinica(1L, 99L, TipoResponsavel.ENFERMEIRO);

        verify(consulta).marcarFalta();
        verify(consultaDAO).atualizar(consulta);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cancelarPelaClinica_deveLancarSemMotivo() {
        service.cancelarPelaClinica(1L, 99L, TipoResponsavel.ENFERMEIRO, "  ");
    }

    @Test
    public void cancelarPelaClinica_deveCancelarELiberarSlot() {
        when(agendaMedicaSlotDAO.buscarComLock(55L)).thenReturn(slot);

        service.cancelarPelaClinica(1L, 99L, TipoResponsavel.ENFERMEIRO, "Emergência hospitalar");

        verify(consulta).cancelar("Emergência hospitalar");
        verify(consultaDAO).atualizar(consulta);
        verify(slot).liberar();
        verify(agendaMedicaSlotDAO).atualizar(slot);
        verify(historicoDAO).salvar(any(ConsultaHistorico.class));
    }

    @Test(expected = IllegalStateException.class)
    public void confirmarChegada_deveLancarSeMedicoTentarFazer() {
        when(consulta.getStatus()).thenReturn(StatusConsulta.AGENDADA);
        service.confirmarChegada(1L, 7L, TipoResponsavel.MEDICO);
    }

    @Test(expected = IllegalStateException.class)
    public void confirmarChegada_deveLancarSePacienteTentarFazer() {
        when(consulta.getStatus()).thenReturn(StatusConsulta.AGENDADA);
        service.confirmarChegada(1L, 10L, TipoResponsavel.PACIENTE);
    }

    @Test(expected = IllegalStateException.class)
    public void marcarFaltaPelaClinica_medicoNaoPodeAtuarEmConsultaDeOutro() {
        service.marcarFaltaPelaClinica(1L, 999L, TipoResponsavel.MEDICO);
    }

    @Test
    public void marcarFaltaPelaClinica_medicoPodeNaPropriaConsulta() {
        service.marcarFaltaPelaClinica(1L, 7L, TipoResponsavel.MEDICO);
        verify(consulta).marcarFalta();
        verify(historicoDAO).salvar(any(ConsultaHistorico.class));
    }

    @Test(expected = IllegalStateException.class)
    public void cancelarPelaClinica_medicoNaoPodeCancelarConsultaDeOutro() {
        service.cancelarPelaClinica(1L, 999L, TipoResponsavel.MEDICO, "tentando interferir");
    }

    @Test
    public void confirmarChegada_deveRegistrarHistoricoEPersist() {
        when(consulta.getStatus()).thenReturn(StatusConsulta.AGENDADA);

        service.confirmarChegada(1L, 22L, TipoResponsavel.ENFERMEIRO);

        verify(consulta).confirmar();
        verify(historicoDAO).salvar(any(ConsultaHistorico.class));
    }

    @Test
    public void marcarRealizada_deveRegistrarHistorico() {
        service.marcarRealizadaComObservacao(1L, 7L, "obs");
        verify(historicoDAO).salvar(any(ConsultaHistorico.class));
    }

    @Test
    public void adicionarAnotacao_medico_consultaPropria_deveSalvar() {
        when(consulta.getStatus()).thenReturn(StatusConsulta.AGENDADA);
        when(anotacaoDAO.salvar(any(ConsultaAnotacao.class))).thenAnswer(i -> i.getArgument(0));

        ConsultaAnotacao a = service.adicionarAnotacao(1L, "Titulo", "Descricao",
                7L, TipoResponsavel.MEDICO);

        assertEquals("Titulo", a.getTitulo());
        verify(anotacaoDAO).salvar(any(ConsultaAnotacao.class));
    }

    @Test(expected = IllegalStateException.class)
    public void adicionarAnotacao_medico_consultaDeOutro_deveRecusar() {
        when(consulta.getStatus()).thenReturn(StatusConsulta.AGENDADA);
        service.adicionarAnotacao(1L, "t", "d", 999L, TipoResponsavel.MEDICO);
    }

    @Test(expected = IllegalStateException.class)
    public void adicionarAnotacao_consultaCancelada_deveRecusar() {
        when(consulta.getStatus()).thenReturn(StatusConsulta.CANCELADA);
        service.adicionarAnotacao(1L, "t", "d", 5L, TipoResponsavel.ENFERMEIRO);
    }

    @Test(expected = IllegalStateException.class)
    public void adicionarAnotacao_paciente_deveRecusar() {
        when(consulta.getStatus()).thenReturn(StatusConsulta.AGENDADA);
        service.adicionarAnotacao(1L, "t", "d", 5L, TipoResponsavel.PACIENTE);
    }

    @Test
    public void adicionarAnotacao_enfermeiro_deveSalvar() {
        when(consulta.getStatus()).thenReturn(StatusConsulta.CONFIRMADA);
        when(anotacaoDAO.salvar(any(ConsultaAnotacao.class))).thenAnswer(i -> i.getArgument(0));

        service.adicionarAnotacao(1L, "Triagem", "PA 120/80, sem alterações", 33L,
                TipoResponsavel.ENFERMEIRO);

        verify(anotacaoDAO).salvar(any(ConsultaAnotacao.class));
    }

    @Test
    public void listarAnotacoes_delegaAoDao() {
        service.listarAnotacoes(1L);
        verify(anotacaoDAO).listarPorConsulta(1L);
    }
}
