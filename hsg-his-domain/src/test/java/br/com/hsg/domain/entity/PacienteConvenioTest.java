package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.TipoTitularidade;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class PacienteConvenioTest {

    @Test
    public void criar_deveIniciarAtivoComTitularidadePadrao() {
        PacienteConvenio pc = PacienteConvenio.criar(mock(Paciente.class), mock(PlanoConvenio.class),
                "hash", "enc", "****", null, null, 1L);
        assertTrue(pc.isAtivo());
        assertEquals(TipoTitularidade.TITULAR, pc.getTipoTitularidade());
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarExcecaoSePacienteNulo() {
        PacienteConvenio.criar(null, mock(PlanoConvenio.class), "h", "e", "****", null, null, 1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarExcecaoSePlanoNulo() {
        PacienteConvenio.criar(mock(Paciente.class), null, "h", "e", "****", null, null, 1L);
    }

    @Test
    public void cancelar_deveTornarInativo() {
        PacienteConvenio pc = PacienteConvenio.criar(mock(Paciente.class), mock(PlanoConvenio.class),
                "hash", "enc", "****", null, TipoTitularidade.DEPENDENTE, 1L);
        pc.cancelar();
        assertFalse(pc.isAtivo());
    }
}
