package br.com.hsg.domain.vo;

import org.junit.Test;

public class EmailTest {

    @Test(expected = IllegalArgumentException.class)
    public void deveLancarExcecaoQuandoEmailInvalido(){
        new Email("email-invalido");
    }

    @Test(expected = IllegalArgumentException.class)
    public void deveLancarExcecaoQuandoEmailNulo(){
        new Email(null);
    }

    @Test
    public void deveCriarEmailValido(){
        new Email("teste@email.com");
    }
}