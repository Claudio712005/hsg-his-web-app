package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.TipoExcecaoAgenda;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class AgendaMedicaExcecaoTest {

    private final Medico medico = mock(Medico.class);

    @Test
    public void criar_deveDefinirCamposEDataCadastro() {
        LocalDateTime ini = LocalDateTime.of(2026, 6, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 6, 8, 0, 0);

        AgendaMedicaExcecao e = AgendaMedicaExcecao.criar(
                medico, ini, fim, TipoExcecaoAgenda.FERIAS, "Férias");

        assertSame(medico, e.getMedico());
        assertEquals(ini, e.getDataInicio());
        assertEquals(fim, e.getDataFim());
        assertEquals(TipoExcecaoAgenda.FERIAS, e.getTipo());
        assertEquals("Férias", e.getMotivo());
        assertNotNull(e.getDataCadastro());
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarQuandoMedicoNulo() {
        AgendaMedicaExcecao.criar(null,
                LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                TipoExcecaoAgenda.FERIAS, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarQuandoDatasNulas() {
        AgendaMedicaExcecao.criar(medico, null, null,
                TipoExcecaoAgenda.FERIAS, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarQuandoFimNaoMaiorQueInicio() {
        LocalDateTime t = LocalDateTime.of(2026, 6, 1, 8, 0);
        AgendaMedicaExcecao.criar(medico, t, t, TipoExcecaoAgenda.FERIAS, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarQuandoTipoNulo() {
        AgendaMedicaExcecao.criar(medico,
                LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                null, null);
    }
}
