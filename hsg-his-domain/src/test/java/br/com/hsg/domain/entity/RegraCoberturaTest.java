package br.com.hsg.domain.entity;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class RegraCoberturaTest {

    @Test
    public void criar_deveIniciarAtivaECoberta() {
        RegraCobertura r = RegraCobertura.criar(mock(PlanoConvenio.class), "Consulta", "cat",
                30, BigDecimal.ZERO, true, null);
        assertTrue(r.isAtivo());
        assertTrue(r.isCoberto());
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarExcecaoSePlanoNulo() {
        RegraCobertura.criar(null, "Consulta", null, 0, BigDecimal.ZERO, true, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarExcecaoSeProcedimentoVazio() {
        RegraCobertura.criar(mock(PlanoConvenio.class), " ", null, 0, BigDecimal.ZERO, true, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarExcecaoSeCarenciaNegativa() {
        RegraCobertura.criar(mock(PlanoConvenio.class), "Consulta", null, -1, BigDecimal.ZERO, true, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarExcecaoSePercentualForaDoIntervalo() {
        RegraCobertura.criar(mock(PlanoConvenio.class), "Consulta", null, 0, new BigDecimal("150"), true, null);
    }

    @Test
    public void criar_deveDefinirNaoCobertoQuandoCoberturaFalse() {
        RegraCobertura r = RegraCobertura.criar(mock(PlanoConvenio.class), "Consulta", null,
                0, BigDecimal.ZERO, false, null);
        assertFalse(r.isCoberto());
    }
}
