package br.com.hsg.domain.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ReceitaItemTest {

    @Test
    public void criar_camposComplet_deveTrim() {
        ReceitaItem ri = ReceitaItem.criar(
                "  Dipirona 500mg  ",
                "  1 cp via oral de 6/6h  ",
                "  Suspender se melhora  ",
                "  r51  ",
                1);
        assertEquals("Dipirona 500mg", ri.getMedicamento());
        assertEquals("1 cp via oral de 6/6h", ri.getPosologia());
        assertEquals("Suspender se melhora", ri.getObservacao());
        assertEquals("R51", ri.getCid10());
        assertEquals(1, ri.getOrdem());
    }

    @Test
    public void criar_observacaoECidOpcionais_devemAceitarNullOuVazio() {
        ReceitaItem ri = ReceitaItem.criar("Amoxicilina 500mg", "1 cp 8/8h", null, "", 2);
        assertNull(ri.getObservacao());
        assertNull(ri.getCid10());
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_medicamentoVazio_deveLancar() {
        ReceitaItem.criar(" ", "1 cp 12/12h", null, null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_medicamentoNull_deveLancar() {
        ReceitaItem.criar(null, "1 cp 12/12h", null, null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_medicamentoLongo_deveLancar() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ReceitaItem.MAX_MEDICAMENTO + 1; i++) sb.append('a');
        ReceitaItem.criar(sb.toString(), "1 cp", null, null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_posologiaVazia_deveLancar() {
        ReceitaItem.criar("Dipirona", " ", null, null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_posologiaLonga_deveLancar() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ReceitaItem.MAX_POSOLOGIA + 1; i++) sb.append('a');
        ReceitaItem.criar("Dipirona", sb.toString(), null, null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_observacaoLonga_deveLancar() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ReceitaItem.MAX_OBSERVACAO + 1; i++) sb.append('a');
        ReceitaItem.criar("Dipirona", "1 cp", sb.toString(), null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_cidLongo_deveLancar() {
        ReceitaItem.criar("Dipirona", "1 cp", null, "MUITOLONGOCID", 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_ordemInvalida_deveLancar() {
        ReceitaItem.criar("Dipirona", "1 cp", null, null, 0);
    }
}
