package br.com.hsg.domain.vo;

import org.junit.Test;

import java.time.LocalDate;

public class DataNascimentoTest {

    @Test(expected = IllegalArgumentException.class)
    public void deveLancarExcecaoQuandoDataNula(){
        new DataNascimento(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deveLancarExcecaoQuandoDataFutura(){
        new DataNascimento(LocalDate.now().plusDays(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deveLancarExcecaoQuandoMenorDeIdade(){
        new DataNascimento(LocalDate.now().minusYears(10));
    }

    @Test
    public void deveCriarQuandoMaiorDeIdade(){
        new DataNascimento(LocalDate.now().minusYears(20));
    }
}