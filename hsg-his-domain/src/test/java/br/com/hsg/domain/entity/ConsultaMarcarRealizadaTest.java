package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.StatusConsulta;
import br.com.hsg.domain.enums.TipoAtendimentoConsulta;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConsultaMarcarRealizadaTest {

    private Paciente paciente;
    private Medico medico;
    private AgendaMedicaSlot slot;

    @Before
    public void setUp() {
        paciente = mock(Paciente.class);
        medico   = mock(Medico.class);
        slot     = mock(AgendaMedicaSlot.class);
        when(slot.getDataInicio()).thenReturn(LocalDateTime.of(2026, 6, 1, 10, 0));
    }

    @Test
    public void marcarRealizadaComObservacao_deveDefinirObservacaoEStatus() {
        Consulta c = nova();

        c.marcarRealizadaComObservacao("  Paciente apresenta hipertensão controlada. Continuar medicação.  ");

        assertEquals(StatusConsulta.REALIZADA, c.getStatus());
        assertEquals("Paciente apresenta hipertensão controlada. Continuar medicação.",
                c.getObservacaoClinica());
        assertNotNull(c.getDataUltimaAtualizacao());
    }

    @Test(expected = IllegalArgumentException.class)
    public void marcarRealizadaComObservacao_deveLancarSemObservacao() {
        Consulta c = nova();
        c.marcarRealizadaComObservacao("   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void marcarRealizadaComObservacao_deveLancarObservacaoMuitoLonga() {
        Consulta c = nova();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1001; i++) sb.append('x');
        c.marcarRealizadaComObservacao(sb.toString());
    }

    @Test(expected = IllegalStateException.class)
    public void marcarRealizadaComObservacao_deveLancarSeJaCancelada() {
        Consulta c = nova();
        c.cancelar("motivo");
        c.marcarRealizadaComObservacao("obs");
    }

    @Test
    public void marcarRealizadaComObservacao_deveAceitarAposConfirmada() {
        Consulta c = nova();
        c.confirmar();
        c.marcarRealizadaComObservacao("ok");
        assertEquals(StatusConsulta.REALIZADA, c.getStatus());
    }

    private Consulta nova() {
        return Consulta.criar(paciente, medico, null, slot, null,
                TipoAtendimentoConsulta.PARTICULAR,
                new BigDecimal("200.00"), new BigDecimal("200.00"), BigDecimal.ZERO);
    }
}
