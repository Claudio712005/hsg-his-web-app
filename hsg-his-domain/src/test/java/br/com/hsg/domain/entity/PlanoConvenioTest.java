package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.TipoCoberturaPlano;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class PlanoConvenioTest {

    @Test
    public void criar_deveIniciarAtivoComAcomodacaoIndividual() {
        Convenio convenio = mock(Convenio.class);
        PlanoConvenio p = PlanoConvenio.criar(convenio, "Plano A", "C1", "desc",
                TipoCoberturaPlano.COMPLETO, BigDecimal.TEN, true);
        assertTrue(p.isAtivo());
        assertTrue(p.isAcomodacaoIndividual());
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarExcecaoSeConvenioNulo() {
        PlanoConvenio.criar(null, "Plano A", null, null, TipoCoberturaPlano.COMPLETO, BigDecimal.TEN, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarExcecaoSeNomeVazio() {
        PlanoConvenio.criar(mock(Convenio.class), " ", null, null, TipoCoberturaPlano.COMPLETO, BigDecimal.TEN, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarExcecaoSeTipoCoberturaNulo() {
        PlanoConvenio.criar(mock(Convenio.class), "Plano A", null, null, null, BigDecimal.TEN, false);
    }

    @Test
    public void inativar_deveAlterarStatus() {
        PlanoConvenio p = PlanoConvenio.criar(mock(Convenio.class), "Plano A", null, null,
                TipoCoberturaPlano.COMPLETO, BigDecimal.TEN, false);
        p.inativar();
        assertFalse(p.isAtivo());
    }
}
