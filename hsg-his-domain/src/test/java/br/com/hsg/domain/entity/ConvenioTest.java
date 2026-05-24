package br.com.hsg.domain.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConvenioTest {

    @Test
    public void criar_deveIniciarAtivo() {
        Convenio c = Convenio.criar("Unimed", "desc", null, null, null, null);
        assertEquals("Unimed", c.getNome());
        assertTrue(c.isAtivo());
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarExcecaoSeNomeVazio() {
        Convenio.criar("  ", null, null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarExcecaoSeNomeExcedeLimite() {
        StringBuilder nome = new StringBuilder();
        for (int i = 0; i < 151; i++) nome.append("a");
        Convenio.criar(nome.toString(), null, null, null, null, null);
    }

    @Test
    public void inativar_deveAlterarStatus() {
        Convenio c = Convenio.criar("Unimed", null, null, null, null, null);
        c.inativar();
        assertFalse(c.isAtivo());
        c.ativar();
        assertTrue(c.isAtivo());
    }
}
