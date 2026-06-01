package br.com.hsg.service.impl.scheduler;

import br.com.hsg.dao.ConsultaDAO;
import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.service.mail.MailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConsultaAutoFaltaServiceImplTest {

    @Mock private ConsultaDAO consultaDAO;
    @Mock private MailService mailService;

    @InjectMocks private ConsultaAutoFaltaServiceImpl service;

    private Medico medico;
    private Paciente paciente;

    @Before
    public void setUp() {
        medico = mock(Medico.class);
        when(medico.getEmail()).thenReturn("medico@hsg.com.br");
        when(medico.getNomeCompleto()).thenReturn("João Silva");

        paciente = mock(Paciente.class);
        when(paciente.getNomeCompleto()).thenReturn("Cláudio Filho");
    }

    @Test
    public void marcarFaltasAutomaticas_semCandidatas_naoFazNada() {
        when(consultaDAO.listarPendentesAteLimite(any())).thenReturn(Collections.emptyList());

        int n = service.marcarFaltasAutomaticas();

        assertEquals(0, n);
        verify(mailService, never()).enviarFaltaAutomaticaParaMedico(any(), any(), any(), any());
    }

    @Test
    public void marcarFaltasAutomaticas_deveMarcarFaltaPersistirEEnviarEmail() {
        Consulta c = mock(Consulta.class);
        when(c.getMedico()).thenReturn(medico);
        when(c.getPaciente()).thenReturn(paciente);
        when(c.getDataConsulta()).thenReturn(LocalDateTime.now().minusDays(2));
        when(consultaDAO.listarPendentesAteLimite(any())).thenReturn(Collections.singletonList(c));

        int n = service.marcarFaltasAutomaticas();

        assertEquals(1, n);
        verify(c).marcarFalta();
        verify(consultaDAO).atualizar(c);
        verify(mailService).enviarFaltaAutomaticaParaMedico(
                eq("João Silva"), eq("medico@hsg.com.br"), eq("Cláudio Filho"), anyString());
    }

    @Test
    public void marcarFaltasAutomaticas_naoEnviaEmailQuandoMedicoSemEmail() {
        when(medico.getEmail()).thenReturn(null);
        Consulta c = mock(Consulta.class);
        when(c.getMedico()).thenReturn(medico);
        when(c.getPaciente()).thenReturn(paciente);
        when(c.getDataConsulta()).thenReturn(LocalDateTime.now().minusDays(2));
        when(consultaDAO.listarPendentesAteLimite(any())).thenReturn(Collections.singletonList(c));

        int n = service.marcarFaltasAutomaticas();

        assertEquals(1, n);
        verify(c).marcarFalta();
        verify(mailService, never()).enviarFaltaAutomaticaParaMedico(any(), any(), any(), any());
    }

    @Test
    public void marcarFaltasAutomaticas_falhaEmUmaNaoInterrompeOutras() {
        Consulta ruim = mock(Consulta.class);
        when(ruim.getMedico()).thenReturn(medico);
        when(ruim.getPaciente()).thenReturn(paciente);
        doThrow(new IllegalStateException("status já final")).when(ruim).marcarFalta();

        Consulta ok = mock(Consulta.class);
        when(ok.getMedico()).thenReturn(medico);
        when(ok.getPaciente()).thenReturn(paciente);
        when(ok.getDataConsulta()).thenReturn(LocalDateTime.now().minusDays(2));

        when(consultaDAO.listarPendentesAteLimite(any())).thenReturn(Arrays.asList(ruim, ok));

        int n = service.marcarFaltasAutomaticas();

        assertEquals(1, n);
        verify(consultaDAO).atualizar(ok);
        verify(consultaDAO, never()).atualizar(ruim);
    }
}
