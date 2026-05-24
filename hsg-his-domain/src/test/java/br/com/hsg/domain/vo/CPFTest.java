package br.com.hsg.domain.vo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CPFTest {

    @Test
    public void deveCriarCpfValido() {
        assertEquals("12345678900", new CPF("12345678900").getValor());
    }

    @Test(expected = IllegalArgumentException.class)
    public void deveLancarExcecaoQuandoNulo() {
        new CPF(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deveLancarExcecaoQuandoComLetras() {
        new CPF("1234567890a");
    }

    @Test(expected = IllegalArgumentException.class)
    public void deveLancarExcecaoQuandoTamanhoInvalido() {
        new CPF("123");
    }

    @Test
    public void deveCompararPorValor() {
        assertEquals(new CPF("12345678900"), new CPF("12345678900"));
        assertNotEquals(new CPF("12345678900"), new CPF("00987654321"));
    }
}
