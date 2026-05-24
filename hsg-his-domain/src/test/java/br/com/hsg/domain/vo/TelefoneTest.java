package br.com.hsg.domain.vo;

import org.junit.Test;

public class TelefoneTest {

    @Test(expected = IllegalArgumentException.class)
    public void deveLancarExcecaoQuandoTelefoneNulo(){
        new Telefone(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deveLancarExcecaoQuandoTamanhoInvalido(){
        new Telefone("123");
    }

    @Test
    public void deveCriarTelefoneValido(){
        new Telefone("11999999999");
    }
}