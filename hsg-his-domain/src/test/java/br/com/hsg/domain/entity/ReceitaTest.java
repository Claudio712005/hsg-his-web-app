package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.IndicativoStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

public class ReceitaTest {

    private Consulta consulta;
    private Medico medico;

    @Before
    public void setUp() {
        consulta = mock(Consulta.class);
        medico   = mock(Medico.class);
    }

    @Test
    public void emitir_caminhoFeliz_deveAtribuirCamposEStatusAtivo() {
        ReceitaItem it1 = ReceitaItem.criar("Dipirona 500mg", "1 cp 6/6h", null, "R51", 1);
        ReceitaItem it2 = ReceitaItem.criar("SRO", "1 sachê 8/8h", "Após cada evacuação", null, 2);

        Receita r = Receita.emitir(consulta, medico, Arrays.asList(it1, it2));

        assertSame(consulta, r.getConsulta());
        assertSame(medico,   r.getMedico());
        assertNotNull(r.getDataEmissao());
        assertEquals(IndicativoStatus.A, r.getStatus());
        assertEquals(2, r.getItens().size());
        assertSame(r, r.getItens().get(0).getReceita());
        assertSame(r, r.getItens().get(1).getReceita());
    }

    @Test
    public void inativar_devePassarStatusParaI() {
        Receita r = Receita.emitir(consulta, medico,
                Collections.singletonList(ReceitaItem.criar("Dipirona", "1 cp", null, null, 1)));
        r.inativar();
        assertEquals(IndicativoStatus.I, r.getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void emitir_consultaNull_deveLancar() {
        Receita.emitir(null, medico,
                Collections.singletonList(ReceitaItem.criar("Dipirona", "1 cp", null, null, 1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void emitir_medicoNull_deveLancar() {
        Receita.emitir(consulta, null,
                Collections.singletonList(ReceitaItem.criar("Dipirona", "1 cp", null, null, 1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void emitir_listaVazia_deveLancar() {
        Receita.emitir(consulta, medico, Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void emitir_listaNull_deveLancar() {
        Receita.emitir(consulta, medico, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emitir_itemNull_deveLancar() {
        Receita.emitir(consulta, medico, Arrays.asList(
                ReceitaItem.criar("Dipirona", "1 cp", null, null, 1),
                null));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void itens_deveSerListaImutavel() {
        Receita r = Receita.emitir(consulta, medico,
                Collections.singletonList(ReceitaItem.criar("Dipirona", "1 cp", null, null, 1)));
        r.getItens().add(ReceitaItem.criar("Outro", "1 cp", null, null, 2));
    }
}
