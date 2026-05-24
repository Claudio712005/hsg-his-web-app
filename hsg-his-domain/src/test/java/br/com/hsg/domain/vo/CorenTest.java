package br.com.hsg.domain.vo;

import br.com.hsg.domain.enums.CategoriaCoren;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CorenTest {

    @Test
    public void deveCriarCorenEFormatar() {
        Coren coren = new Coren("54321", "rj", CategoriaCoren.ENF);
        assertEquals("54321", coren.getNumero());
        assertEquals("RJ", coren.getUf());
        assertEquals("COREN-RJ 54321/ENF", coren.getFormatado());
    }

    @Test(expected = IllegalArgumentException.class)
    public void deveLancarExcecaoQuandoNumeroVazio() {
        new Coren(" ", "RJ", CategoriaCoren.ENF);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deveLancarExcecaoQuandoUfNula() {
        new Coren("54321", null, CategoriaCoren.ENF);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deveLancarExcecaoQuandoCategoriaNula() {
        new Coren("54321", "RJ", null);
    }
}
