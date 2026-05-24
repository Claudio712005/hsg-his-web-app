package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.IndicativoStatus;
import org.junit.Test;

import static org.junit.Assert.*;

public class EspecialidadeTest {

    @Test
    public void criar_deveDefinirStatusAtivoEDataCadastro() {
        Especialidade e = Especialidade.criar("Cardiologia", "Coração", "CLINICA");

        assertEquals("Cardiologia", e.getNome());
        assertEquals("Coração", e.getDescricao());
        assertEquals("CLINICA", e.getArea());
        assertEquals(IndicativoStatus.A, e.getStatus());
        assertNotNull(e.getDataCadastro());
        assertTrue(e.isAtiva());
    }

    @Test
    public void criar_deveFazerTrimNoNome() {
        Especialidade e = Especialidade.criar("  Pediatria  ", null, null);
        assertEquals("Pediatria", e.getNome());
    }

    @Test(expected = NullPointerException.class)
    public void criar_deveLancarExcecaoQuandoNomeNulo() {
        Especialidade.criar(null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarExcecaoQuandoNomeVazio() {
        Especialidade.criar("   ", null, null);
    }
}
