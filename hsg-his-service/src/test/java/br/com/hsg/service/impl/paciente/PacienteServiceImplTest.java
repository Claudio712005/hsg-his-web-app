package br.com.hsg.service.impl.paciente;

import br.com.hsg.dao.PacienteDAO;
import br.com.hsg.domain.entity.Paciente;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PacienteServiceImplTest {

    @Mock private PacienteDAO pacienteDAO;
    @InjectMocks private PacienteServiceImpl service;

    @Test
    public void buscarPorKeycloakId_deveDelegarAoDAO() {
        Paciente p = mock(Paciente.class);
        when(pacienteDAO.buscarPorKeycloakId("kc")).thenReturn(p);
        assertSame(p, service.buscarPorKeycloakId("kc"));
    }

    @Test
    public void buscarPorId_deveDelegarAoDAO() {
        Paciente p = mock(Paciente.class);
        when(pacienteDAO.buscarPorId(1L)).thenReturn(p);
        assertSame(p, service.buscarPorId(1L));
    }
}
