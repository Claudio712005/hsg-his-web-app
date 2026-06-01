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

public class ConsultaTest {

    private Paciente paciente;
    private Medico medico;
    private AgendaMedicaSlot slot;
    private LocalDateTime quando;

    @Before
    public void setUp() {
        paciente = mock(Paciente.class);
        medico   = mock(Medico.class);
        slot     = mock(AgendaMedicaSlot.class);
        quando   = LocalDateTime.of(2026, 6, 1, 10, 0);
        when(slot.getDataInicio()).thenReturn(quando);
    }

    @Test
    public void criar_particular_deveDefinirStatusAgendadaEDataDoSlot() {
        Consulta c = Consulta.criar(paciente, medico, null, slot, null,
                TipoAtendimentoConsulta.PARTICULAR,
                new BigDecimal("200.00"), new BigDecimal("200.00"), BigDecimal.ZERO);

        assertEquals(StatusConsulta.AGENDADA, c.getStatus());
        assertEquals(quando, c.getDataConsulta());
        assertEquals(TipoAtendimentoConsulta.PARTICULAR, c.getTipoAtendimento());
        assertNotNull(c.getDataCadastro());
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarQuandoPacienteNulo() {
        Consulta.criar(null, medico, null, slot, null, TipoAtendimentoConsulta.PARTICULAR,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarQuandoMedicoNulo() {
        Consulta.criar(paciente, null, null, slot, null, TipoAtendimentoConsulta.PARTICULAR,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarQuandoSlotNulo() {
        Consulta.criar(paciente, medico, null, null, null, TipoAtendimentoConsulta.PARTICULAR,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarQuandoTipoNulo() {
        Consulta.criar(paciente, medico, null, slot, null, null,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_convenioSemPacienteConvenio_deveLancar() {
        Consulta.criar(paciente, medico, null, slot, null, TipoAtendimentoConsulta.CONVENIO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Test
    public void confirmar_deveTransicionarAgendadaParaConfirmada() {
        Consulta c = novaConsulta();
        c.confirmar();
        assertTrue(c.isConfirmada());
        assertNotNull(c.getDataUltimaAtualizacao());
    }

    @Test(expected = IllegalStateException.class)
    public void confirmar_deveLancarSeNaoAgendada() {
        Consulta c = novaConsulta();
        c.cancelar("motivo");
        c.confirmar();
    }

    @Test
    public void marcarRealizada_deveAceitarAgendadaOuConfirmada() {
        Consulta c = novaConsulta();
        c.confirmar();
        c.marcarRealizada();
        assertEquals(StatusConsulta.REALIZADA, c.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void marcarRealizada_deveLancarSeJaCancelada() {
        Consulta c = novaConsulta();
        c.cancelar("motivo");
        c.marcarRealizada();
    }

    @Test
    public void marcarFalta_deveAtualizarStatus() {
        Consulta c = novaConsulta();
        c.marcarFalta();
        assertEquals(StatusConsulta.FALTOU, c.getStatus());
    }

    @Test
    public void cancelar_deveSalvarMotivoEData() {
        Consulta c = novaConsulta();
        c.cancelar("  imprevisto  ");
        assertTrue(c.isCancelada());
        assertEquals("imprevisto", c.getMotivoCancelamento());
        assertNotNull(c.getDataCancelamento());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cancelar_deveLancarSemMotivo() {
        Consulta c = novaConsulta();
        c.cancelar("   ");
    }

    @Test(expected = IllegalStateException.class)
    public void cancelar_deveLancarSeJaRealizada() {
        Consulta c = novaConsulta();
        c.confirmar();
        c.marcarRealizada();
        c.cancelar("motivo");
    }

    private Consulta novaConsulta() {
        return Consulta.criar(paciente, medico, null, slot, null,
                TipoAtendimentoConsulta.PARTICULAR,
                new BigDecimal("200.00"), new BigDecimal("200.00"), BigDecimal.ZERO);
    }
}
