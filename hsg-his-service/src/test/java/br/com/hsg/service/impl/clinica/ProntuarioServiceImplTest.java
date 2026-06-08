package br.com.hsg.service.impl.clinica;

import br.com.hsg.dao.AlergiaDAO;
import br.com.hsg.dao.ArquivoDAO;
import br.com.hsg.dao.ConsultaAnotacaoDAO;
import br.com.hsg.dao.ConsultaDAO;
import br.com.hsg.dao.PacienteDAO;
import br.com.hsg.dao.ReceitaDAO;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.domain.enums.TipoResponsavel;
import br.com.hsg.service.dto.prontuario.ProntuarioDTO;
import br.com.hsg.service.facade.clinica.ProntuarioServiceFacade.PacienteBuscaDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ProntuarioServiceImplTest {

    @Mock private PacienteDAO          pacienteDAO;
    @Mock private ConsultaDAO          consultaDAO;
    @Mock private ConsultaAnotacaoDAO  anotacaoDAO;
    @Mock private ArquivoDAO           arquivoDAO;
    @Mock private ReceitaDAO           receitaDAO;
    @Mock private AlergiaDAO           alergiaDAO;

    @InjectMocks private ProntuarioServiceImpl service;

    private Paciente paciente;

    @Before
    public void setUp() {
        paciente = mock(Paciente.class);
        when(paciente.getId()).thenReturn(100L);
        when(paciente.getNomeCompleto()).thenReturn("Claudio Filho");
        when(pacienteDAO.buscarPorId(100L)).thenReturn(paciente);
        when(consultaDAO.listarPorPaciente(anyLong())).thenReturn(Collections.emptyList());
        when(alergiaDAO.listarPorPaciente(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
    }

    @Test
    public void montar_admin_deveRetornarProntuario() {
        ProntuarioDTO dto = service.montarParaPaciente(100L, 1L, TipoResponsavel.ADMIN);
        assertNotNull(dto);
        assertNotNull(dto.getPaciente());
        assertEquals("Claudio Filho", dto.getPaciente().getNomeCompleto());
    }

    @Test
    public void montar_enfermeiro_deveRetornarProntuario() {
        ProntuarioDTO dto = service.montarParaPaciente(100L, 33L, TipoResponsavel.ENFERMEIRO);
        assertNotNull(dto);
    }

    @Test
    public void montar_medico_qualquer_deveRetornarProntuario() {
        ProntuarioDTO dto = service.montarParaPaciente(100L, 7L, TipoResponsavel.MEDICO);
        assertNotNull(dto);
    }

    @Test
    public void montar_pacienteProprio_deveRetornarProntuario() {
        ProntuarioDTO dto = service.montarParaPaciente(100L, 100L, TipoResponsavel.PACIENTE);
        assertNotNull(dto);
    }

    @Test(expected = IllegalStateException.class)
    public void montar_pacienteOutro_deveRecusar() {
        service.montarParaPaciente(100L, 999L, TipoResponsavel.PACIENTE);
    }

    @Test(expected = IllegalStateException.class)
    public void montar_sistema_deveRecusar() {
        service.montarParaPaciente(100L, 1L, TipoResponsavel.SISTEMA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void montar_idNull_deveLancar() {
        service.montarParaPaciente(null, 1L, TipoResponsavel.ADMIN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void montar_pacienteInexistente_deveLancar() {
        when(pacienteDAO.buscarPorId(99L)).thenReturn(null);
        service.montarParaPaciente(99L, 1L, TipoResponsavel.ADMIN);
    }

    @Test
    public void buscar_medico_termoValido_retornaLista() {
        Paciente p = mock(Paciente.class);
        when(p.getId()).thenReturn(100L);
        when(p.getNomeCompleto()).thenReturn("Claudio Filho");
        when(pacienteDAO.buscarPorTermo(anyString(), anyInt())).thenReturn(Arrays.asList(p));

        List<PacienteBuscaDTO> out = service.buscarPacientes("claudio", 7L,
                TipoResponsavel.MEDICO, 10);
        assertEquals(1, out.size());
        assertEquals("Claudio Filho", out.get(0).nomeCompleto);
    }

    @Test
    public void buscar_termoVazio_retornaListaVazia() {
        assertEquals(0, service.buscarPacientes(" ", 7L,
                TipoResponsavel.MEDICO, 10).size());
    }

    @Test(expected = IllegalStateException.class)
    public void buscar_paciente_naoPodeBuscar() {
        service.buscarPacientes("xyz", 100L, TipoResponsavel.PACIENTE, 10);
    }

    @Test(expected = IllegalStateException.class)
    public void buscar_sistema_naoPodeBuscar() {
        service.buscarPacientes("xyz", 1L, TipoResponsavel.SISTEMA, 10);
    }
}
