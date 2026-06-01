package br.com.hsg.service.impl.paciente;

import br.com.hsg.dao.AgendaMedicaSlotDAO;
import br.com.hsg.dao.ConsultaDAO;
import br.com.hsg.dao.PacienteConvenioDAO;
import br.com.hsg.dao.PacienteDAO;
import br.com.hsg.dao.RegraCoberturaDAO;
import br.com.hsg.domain.entity.AgendaMedicaSlot;
import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.domain.entity.PacienteConvenio;
import br.com.hsg.domain.entity.PlanoConvenio;
import br.com.hsg.domain.entity.RegraCobertura;
import br.com.hsg.domain.enums.StatusConsulta;
import br.com.hsg.domain.enums.StatusSlotAgenda;
import br.com.hsg.domain.enums.TipoAtendimentoConsulta;
import br.com.hsg.service.dto.ResultadoFinanceiroConsulta;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConsultaServiceImplTest {

    @Mock private EntityManager em;
    @Mock private ConsultaDAO consultaDAO;
    @Mock private AgendaMedicaSlotDAO agendaMedicaSlotDAO;
    @Mock private PacienteDAO pacienteDAO;
    @Mock private PacienteConvenioDAO pacienteConvenioDAO;
    @Mock private RegraCoberturaDAO regraCoberturaDAO;

    @InjectMocks private ConsultaServiceImpl service;

    private Paciente paciente;
    private Medico medico;
    private AgendaMedicaSlot slot;

    @Before
    public void setUp() {
        paciente = mock(Paciente.class);
        when(paciente.getId()).thenReturn(10L);

        medico = mock(Medico.class);
        when(medico.getValorConsulta()).thenReturn(new BigDecimal("200.00"));

        slot = mock(AgendaMedicaSlot.class);
        when(slot.getId()).thenReturn(55L);
        when(slot.getMedico()).thenReturn(medico);
        when(slot.getStatus()).thenReturn(StatusSlotAgenda.LIVRE);
        when(slot.getDataInicio()).thenReturn(LocalDateTime.now().plusDays(3));

        when(pacienteDAO.buscarPorId(10L)).thenReturn(paciente);
        when(agendaMedicaSlotDAO.buscarComLock(55L)).thenReturn(slot);
        when(consultaDAO.contarFuturasAtivasPorPaciente(eq(10L), any())).thenReturn(0L);
        when(consultaDAO.salvar(any(Consulta.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test(expected = IllegalStateException.class)
    public void agendar_deveLancarQuandoSlotNaoLivre() {
        when(slot.getStatus()).thenReturn(StatusSlotAgenda.RESERVADO);
        service.agendar(10L, null, 55L, false);
    }

    @Test(expected = IllegalStateException.class)
    public void agendar_deveLancarQuandoAntecedenciaInsuficiente() {
        when(slot.getDataInicio()).thenReturn(LocalDateTime.now().plusMinutes(30));
        service.agendar(10L, null, 55L, false);
    }

    @Test(expected = IllegalStateException.class)
    public void agendar_deveLancarQuandoForaDaJanelaMaxima() {
        when(slot.getDataInicio()).thenReturn(LocalDateTime.now().plusDays(100));
        service.agendar(10L, null, 55L, false);
    }

    @Test(expected = IllegalStateException.class)
    public void agendar_deveLancarQuandoLimiteFuturasAtingido() {
        when(consultaDAO.contarFuturasAtivasPorPaciente(eq(10L), any())).thenReturn(3L);
        service.agendar(10L, null, 55L, false);
    }

    @Test
    public void agendar_particular_deveSalvarComValorCheioESemCobertura() {
        Consulta c = service.agendar(10L, null, 55L, false);

        assertEquals(TipoAtendimentoConsulta.PARTICULAR, c.getTipoAtendimento());
        assertEquals(new BigDecimal("200.00"), c.getValorConsulta());
        assertEquals(new BigDecimal("200.00"), c.getValorCopagamento());
        assertEquals(BigDecimal.ZERO, c.getValorCoberturaConvenio());
        assertEquals(StatusConsulta.AGENDADA, c.getStatus());
        verify(slot).reservar(any());
        verify(agendaMedicaSlotDAO).atualizar(slot);
    }

    @Test
    public void agendar_convenio_foraCarencia_deveCalcularCopagamento() {
        prepararConvenio(0, new BigDecimal("30.00"), true);

        Consulta c = service.agendar(10L, null, 55L, true);

        assertEquals(TipoAtendimentoConsulta.CONVENIO, c.getTipoAtendimento());
        assertEquals(new BigDecimal("200.00"), c.getValorConsulta());
        assertEquals(new BigDecimal("60.00"), c.getValorCopagamento());
        assertEquals(new BigDecimal("140.00"), c.getValorCoberturaConvenio());
    }

    @Test(expected = IllegalStateException.class)
    public void agendar_convenio_emCarencia_deveLancar() {
        prepararConvenio(30, new BigDecimal("30.00"), true);
        service.agendar(10L, null, 55L, true);
    }

    @Test(expected = IllegalStateException.class)
    public void agendar_convenio_semConvenioAtivo_deveLancar() {
        when(pacienteConvenioDAO.buscarAtivoPorPaciente(10L)).thenReturn(null);
        service.agendar(10L, null, 55L, true);
    }

    @Test
    public void simular_convenio_emCarencia_deveRetornarParticularComFlag() {
        prepararConvenio(30, new BigDecimal("30.00"), true);
        when(agendaMedicaSlotDAO.buscarPorId(55L)).thenReturn(slot);

        ResultadoFinanceiroConsulta r = service.simular(10L, 55L, true);

        assertEquals(TipoAtendimentoConsulta.PARTICULAR, r.getTipoAtendimento());
        assertTrue(r.isEmCarencia());
        assertFalse(r.isConvenioDisponivel());
    }

    @Test(expected = IllegalStateException.class)
    public void cancelar_deveLancarQuandoMenosDe24h() {
        Consulta c = mock(Consulta.class);
        when(c.getPaciente()).thenReturn(paciente);
        when(c.getDataConsulta()).thenReturn(LocalDateTime.now().plusHours(5));
        when(consultaDAO.buscarPorId(1L)).thenReturn(c);

        service.cancelarPeloPaciente(1L, 10L, "imprevisto");
    }

    @Test(expected = IllegalStateException.class)
    public void cancelar_deveLancarQuandoConsultaDeOutroPaciente() {
        Paciente outro = mock(Paciente.class);
        when(outro.getId()).thenReturn(999L);
        Consulta c = mock(Consulta.class);
        when(c.getPaciente()).thenReturn(outro);
        when(consultaDAO.buscarPorId(1L)).thenReturn(c);

        service.cancelarPeloPaciente(1L, 10L, "motivo");
    }

    @Test
    public void cancelar_deveCancelarELiberarSlot() {
        Consulta c = mock(Consulta.class);
        when(c.getPaciente()).thenReturn(paciente);
        when(c.getDataConsulta()).thenReturn(LocalDateTime.now().plusDays(5));
        when(c.getSlot()).thenReturn(slot);
        when(consultaDAO.buscarPorId(1L)).thenReturn(c);
        when(agendaMedicaSlotDAO.buscarComLock(55L)).thenReturn(slot);

        service.cancelarPeloPaciente(1L, 10L, "imprevisto");

        verify(c).cancelar("imprevisto");
        verify(slot).liberar();
        verify(agendaMedicaSlotDAO).atualizar(slot);
    }

    @Test
    public void simular_particular_deveRetornarValorCheio() {
        when(agendaMedicaSlotDAO.buscarPorId(55L)).thenReturn(slot);

        ResultadoFinanceiroConsulta r = service.simular(10L, 55L, false);

        assertEquals(TipoAtendimentoConsulta.PARTICULAR, r.getTipoAtendimento());
        assertEquals(new BigDecimal("200.00"), r.getValorConsulta());
        assertEquals(new BigDecimal("200.00"), r.getValorCopagamento());
        assertEquals(BigDecimal.ZERO, r.getValorCoberturaConvenio());
        assertFalse(r.isConvenioDisponivel());
    }

    @Test
    public void simular_convenio_semCobertura_deveCairParaParticular() {
        prepararConvenio(0, new BigDecimal("30.00"), false);
        when(agendaMedicaSlotDAO.buscarPorId(55L)).thenReturn(slot);

        ResultadoFinanceiroConsulta r = service.simular(10L, 55L, true);

        assertEquals(TipoAtendimentoConsulta.PARTICULAR, r.getTipoAtendimento());
        assertFalse(r.isConvenioDisponivel());
        assertFalse(r.isEmCarencia());
    }

    @Test(expected = IllegalArgumentException.class)
    public void simular_deveLancarQuandoSlotNaoEncontrado() {
        when(agendaMedicaSlotDAO.buscarPorId(55L)).thenReturn(null);
        service.simular(10L, 55L, false);
    }

    @Test(expected = IllegalStateException.class)
    public void agendar_convenio_semCobertura_deveLancarEmStrict() {
        prepararConvenio(0, new BigDecimal("30.00"), false);
        service.agendar(10L, null, 55L, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void agendar_deveLancarQuandoPacienteNaoEncontrado() {
        when(pacienteDAO.buscarPorId(10L)).thenReturn(null);
        service.agendar(10L, null, 55L, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void agendar_deveLancarQuandoSlotNaoEncontrado() {
        when(agendaMedicaSlotDAO.buscarComLock(55L)).thenReturn(null);
        service.agendar(10L, null, 55L, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cancelar_deveLancarQuandoConsultaNaoEncontrada() {
        when(consultaDAO.buscarPorId(1L)).thenReturn(null);
        service.cancelarPeloPaciente(1L, 10L, "motivo");
    }

    @Test
    public void listarConsultasPaciente_deveDelegarAoDao() {
        service.listarConsultasPaciente(10L);
        verify(consultaDAO).listarPorPaciente(10L);
    }

    private void prepararConvenio(int carenciaDias, BigDecimal pct, boolean coberto) {
        PlanoConvenio plano = mock(PlanoConvenio.class);
        when(plano.getId()).thenReturn(7L);
        PacienteConvenio convenio = mock(PacienteConvenio.class);
        when(convenio.getPlano()).thenReturn(plano);
        when(convenio.getDataAdesao()).thenReturn(LocalDateTime.now());
        when(pacienteConvenioDAO.buscarAtivoPorPaciente(10L)).thenReturn(convenio);

        RegraCobertura regra = mock(RegraCobertura.class);
        when(regra.getCategoria()).thenReturn("Consultas");
        when(regra.getProcedimento()).thenReturn("Consulta médica eletiva");
        when(regra.isCoberto()).thenReturn(coberto);
        when(regra.getCarenciaDias()).thenReturn(carenciaDias);
        when(regra.getPercentualCopagamento()).thenReturn(pct);
        when(regraCoberturaDAO.listarAtivasPorPlano(7L))
                .thenReturn(Collections.singletonList(regra));
    }
}
