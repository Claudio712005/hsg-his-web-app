package br.com.hsg.service.impl;

import br.com.hsg.dao.AlergiaDAO;
import br.com.hsg.dao.AlergiaHistoricoDAO;
import br.com.hsg.dao.PacienteDAO;
import br.com.hsg.domain.entity.Alergia;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.domain.enums.GravidadeAlergia;
import br.com.hsg.domain.enums.StatusAlergia;
import br.com.hsg.domain.enums.TipoAlergia;
import br.com.hsg.service.impl.paciente.AlergiaServiceImpl;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AlergiaServiceImplTest {

    private AlergiaServiceImpl service;
    private AlergiaDAO alergiaDAO;
    private AlergiaHistoricoDAO historicoDAO;
    private PacienteDAO pacienteDAO;

    @Before
    public void setUp() throws Exception {
        service      = new AlergiaServiceImpl();
        alergiaDAO   = mock(AlergiaDAO.class);
        historicoDAO = mock(AlergiaHistoricoDAO.class);
        pacienteDAO  = mock(PacienteDAO.class);

        injetarCampo(service, "alergiaDAO",  alergiaDAO);
        injetarCampo(service, "historicoDAO", historicoDAO);
        injetarCampo(service, "pacienteDAO",  pacienteDAO);
    }

    private void injetarCampo(Object alvo, String nomeCampo, Object valor) throws Exception {
        Field campo = alvo.getClass().getDeclaredField(nomeCampo);
        campo.setAccessible(true);
        campo.set(alvo, valor);
    }

    private Paciente pacienteMock() {
        return mock(Paciente.class);
    }

    private Alergia alergiaInformada(Paciente paciente) {
        return Alergia.informar(paciente, 1L, "Dipirona", null,
                TipoAlergia.M, GravidadeAlergia.G, null, null, null);
    }


    @Test
    public void informarAlergia_deveSalvarAlergiaEHistorico() {
        Paciente paciente = pacienteMock();
        when(pacienteDAO.buscarPorId(1L)).thenReturn(paciente);

        service.informarAlergia(1L, 2L, "Dipirona", null,
                TipoAlergia.M, GravidadeAlergia.G, null, null, null);

        verify(alergiaDAO, times(1)).salvar(any(Alergia.class));
        verify(historicoDAO, times(1)).salvar(any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void informarAlergia_deveLancarExcecaoSePacienteNaoEncontrado() {
        when(pacienteDAO.buscarPorId(999L)).thenReturn(null);

        service.informarAlergia(999L, 2L, "Dipirona", null,
                TipoAlergia.M, GravidadeAlergia.G, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void informarAlergia_deveLancarExcecaoSeNomeNulo() {
        service.informarAlergia(1L, 2L, null, null,
                TipoAlergia.M, GravidadeAlergia.G, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void informarAlergia_deveLancarExcecaoSeTipoNulo() {
        service.informarAlergia(1L, 2L, "Dipirona", null,
                null, GravidadeAlergia.G, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void informarAlergia_deveLancarExcecaoSeGravidadeNula() {
        service.informarAlergia(1L, 2L, "Dipirona", null,
                TipoAlergia.M, null, null, null, null);
    }


    @Test
    public void aprovarAlergia_deveAtualizarAlergiaESalvarHistorico() {
        Paciente paciente = pacienteMock();
        Alergia alergia   = alergiaInformada(paciente);
        when(alergiaDAO.buscarPorId(10L)).thenReturn(alergia);

        service.aprovarAlergia(10L, 99L, "Confirmado");

        assertEquals(StatusAlergia.APROVADA, alergia.getStatusAlergia());
        verify(alergiaDAO, times(1)).atualizar(alergia);
        verify(historicoDAO, times(1)).salvar(any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void aprovarAlergia_deveLancarExcecaoSeAlergiaNaoEncontrada() {
        when(alergiaDAO.buscarPorId(999L)).thenReturn(null);
        service.aprovarAlergia(999L, 99L, "ok");
    }


    @Test
    public void rejeitarAlergia_deveAtualizarAlergiaESalvarHistorico() {
        Paciente paciente = pacienteMock();
        Alergia alergia   = alergiaInformada(paciente);
        when(alergiaDAO.buscarPorId(10L)).thenReturn(alergia);

        service.rejeitarAlergia(10L, 99L, "Duplicada");

        assertEquals(StatusAlergia.REJEITADA, alergia.getStatusAlergia());
        verify(alergiaDAO, times(1)).atualizar(alergia);
        verify(historicoDAO, times(1)).salvar(any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejeitarAlergia_deveLancarExcecaoSeAlergiaNaoEncontrada() {
        when(alergiaDAO.buscarPorId(999L)).thenReturn(null);
        service.rejeitarAlergia(999L, 99L, "ok");
    }

    @Test
    public void excluirAlergia_deveExcluirAlergiaInformada() {
        Paciente paciente = pacienteMock();
        Alergia alergia   = alergiaInformada(paciente);
        when(alergiaDAO.buscarPorId(10L)).thenReturn(alergia);

        service.excluirAlergia(10L, 2L);

        verify(alergiaDAO, times(1)).excluir(alergia);
        verify(historicoDAO, times(1)).salvar(any());
    }

    @Test(expected = IllegalStateException.class)
    public void excluirAlergia_deveLancarExcecaoSeAlergiaAprovada() {
        Paciente paciente = pacienteMock();
        Alergia alergia   = alergiaInformada(paciente);
        alergia.aprovar(99L, "aprovada");
        when(alergiaDAO.buscarPorId(10L)).thenReturn(alergia);

        service.excluirAlergia(10L, 2L);
    }
}
