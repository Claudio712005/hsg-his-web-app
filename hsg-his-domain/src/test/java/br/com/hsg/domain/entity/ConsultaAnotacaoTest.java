package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.TipoResponsavel;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ConsultaAnotacaoTest {

    private final Consulta consulta = mock(Consulta.class);

    @Test
    public void registrar_camposPreenchidos() {
        ConsultaAnotacao a = ConsultaAnotacao.registrar(consulta,
                "  Conduta  ", "  Manter dieta  ", 7L, TipoResponsavel.MEDICO);

        assertSame(consulta, a.getConsulta());
        assertEquals("Conduta", a.getTitulo());
        assertEquals("Manter dieta", a.getDescricao());
        assertEquals(Long.valueOf(7L), a.getIdResponsavel());
        assertEquals(TipoResponsavel.MEDICO, a.getTipoResponsavel());
        assertNotNull(a.getDataCriacao());
    }

    @Test(expected = IllegalArgumentException.class)
    public void registrar_consultaNula() {
        ConsultaAnotacao.registrar(null, "t", "d", 1L, TipoResponsavel.MEDICO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registrar_tituloVazio() {
        ConsultaAnotacao.registrar(consulta, "   ", "d", 1L, TipoResponsavel.MEDICO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registrar_tituloLongo() {
        StringBuilder t = new StringBuilder();
        for (int i = 0; i < 201; i++) t.append('x');
        ConsultaAnotacao.registrar(consulta, t.toString(), "d", 1L, TipoResponsavel.MEDICO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registrar_descricaoVazia() {
        ConsultaAnotacao.registrar(consulta, "t", "   ", 1L, TipoResponsavel.MEDICO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registrar_descricaoLonga() {
        StringBuilder d = new StringBuilder();
        for (int i = 0; i < 2001; i++) d.append('x');
        ConsultaAnotacao.registrar(consulta, "t", d.toString(), 1L, TipoResponsavel.MEDICO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registrar_responsavelNulo() {
        ConsultaAnotacao.registrar(consulta, "t", "d", null, TipoResponsavel.MEDICO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registrar_tipoNulo() {
        ConsultaAnotacao.registrar(consulta, "t", "d", 1L, null);
    }
}
