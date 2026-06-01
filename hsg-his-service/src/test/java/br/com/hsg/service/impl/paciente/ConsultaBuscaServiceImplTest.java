package br.com.hsg.service.impl.paciente;

import br.com.hsg.dao.AgendaMedicaSlotDAO;
import br.com.hsg.dao.EspecialidadeDAO;
import br.com.hsg.dao.MedicoEspecialidadeDAO;
import br.com.hsg.dao.PacienteConvenioDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConsultaBuscaServiceImplTest {

    @Mock private EspecialidadeDAO especialidadeDAO;
    @Mock private MedicoEspecialidadeDAO medicoEspecialidadeDAO;
    @Mock private AgendaMedicaSlotDAO agendaMedicaSlotDAO;
    @Mock private PacienteConvenioDAO pacienteConvenioDAO;

    @InjectMocks private ConsultaBuscaServiceImpl service;

    @Test(expected = IllegalArgumentException.class)
    public void listarHorariosLivresProximos_deveLancarSemEspecialidade() {
        service.listarHorariosLivresProximos(null, 14, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void listarHorariosLivresProximos_deveLancarQuandoDiasInvalido() {
        service.listarHorariosLivresProximos(1L, 0, null);
    }

    @Test
    public void listarHorariosLivresProximos_deveDelegarAoDao() {
        service.listarHorariosLivresProximos(1L, 14, 9L);

        verify(agendaMedicaSlotDAO).listarLivresPorEspecialidadeData(eq(1L), any(), any(), eq(9L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void listarMedicosPorEspecialidade_deveLancarSemEspecialidade() {
        service.listarMedicosPorEspecialidade(null);
    }

    @Test
    public void listarMedicosPorEspecialidade_deveDelegarAoDao() {
        service.listarMedicosPorEspecialidade(7L);
        verify(medicoEspecialidadeDAO).listarMedicosPorEspecialidade(7L);
    }

    @Test
    public void listarEspecialidadesAtivas_deveDelegarAoDao() {
        service.listarEspecialidadesAtivas();
        verify(especialidadeDAO).listarAtivas();
    }

    @Test
    public void buscarConvenioAtivo_idNulo_deveRetornarNull() {
        org.junit.Assert.assertNull(service.buscarConvenioAtivo(null));
    }

    @Test
    public void buscarConvenioAtivo_deveDelegarAoDao() {
        service.buscarConvenioAtivo(10L);
        verify(pacienteConvenioDAO).buscarAtivoPorPaciente(10L);
    }
}
