package br.com.hsg.service.impl.clinica;

import br.com.hsg.dao.EnfermeiroDAO;
import br.com.hsg.dao.EspecialidadeDAO;
import br.com.hsg.dao.MedicoDAO;
import br.com.hsg.domain.entity.Especialidade;
import br.com.hsg.domain.entity.Medico;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ClinicaServiceImplTest {

    @Mock private EnfermeiroDAO enfermeiroDAO;
    @Mock private MedicoDAO medicoDAO;
    @Mock private EspecialidadeDAO especialidadeDAO;

    @InjectMocks private ClinicaServiceImpl service;

    @Test
    public void buscarMedicoPorKeycloakId_deveDelegarAoDAO() {
        Medico m = mock(Medico.class);
        when(medicoDAO.buscarPorKeycloakId("kc")).thenReturn(m);
        assertSame(m, service.buscarMedicoPorKeycloakId("kc"));
    }

    @Test
    public void contarMedicos_deveDelegarAoDAO() {
        when(medicoDAO.contarTotal("nome", "A")).thenReturn(2L);
        assertEquals(2L, service.contarMedicos("nome", "A"));
    }

    @Test
    public void contarEspecialidadesAtivas_deveContarPelaListaAtiva() {
        when(especialidadeDAO.listarAtivas())
                .thenReturn(Arrays.asList(mock(Especialidade.class), mock(Especialidade.class)));
        assertEquals(2L, service.contarEspecialidadesAtivas());
    }
}
