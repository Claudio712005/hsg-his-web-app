package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.DiaSemana;
import br.com.hsg.domain.enums.IndicativoStatus;
import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class AgendaMedicaTest {

    private final Medico medico = mock(Medico.class);

    @Test
    public void criar_deveDefinirCamposEStatusAtivo() {
        AgendaMedica a = AgendaMedica.criar(medico, DiaSemana.SEGUNDA,
                LocalTime.of(8, 0), LocalTime.of(12, 0), 30);

        assertEquals(DiaSemana.SEGUNDA, a.getDiaSemana());
        assertEquals(LocalTime.of(8, 0), a.getHoraInicio());
        assertEquals(LocalTime.of(12, 0), a.getHoraFim());
        assertEquals(Integer.valueOf(30), a.getDuracaoMinutos());
        assertEquals(IndicativoStatus.A, a.getStatus());
        assertNotNull(a.getDataCadastro());
        assertTrue(a.isAtiva());
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarQuandoMedicoNulo() {
        AgendaMedica.criar(null, DiaSemana.SEGUNDA,
                LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarQuandoDiaSemanaNulo() {
        AgendaMedica.criar(medico, null,
                LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarQuandoFimNaoMaiorQueInicio() {
        AgendaMedica.criar(medico, DiaSemana.SEGUNDA,
                LocalTime.of(12, 0), LocalTime.of(12, 0), 30);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarQuandoDuracaoZeroOuNegativa() {
        AgendaMedica.criar(medico, DiaSemana.SEGUNDA,
                LocalTime.of(8, 0), LocalTime.of(12, 0), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarQuandoFaixaMenorQueDuracao() {
        AgendaMedica.criar(medico, DiaSemana.SEGUNDA,
                LocalTime.of(8, 0), LocalTime.of(8, 20), 30);
    }

    @Test
    public void atualizar_deveTrocarHorariosEMarcarDataAtualizacao() {
        AgendaMedica a = AgendaMedica.criar(medico, DiaSemana.SEGUNDA,
                LocalTime.of(8, 0), LocalTime.of(12, 0), 30);

        a.atualizar(LocalTime.of(9, 0), LocalTime.of(13, 0), 60);

        assertEquals(LocalTime.of(9, 0), a.getHoraInicio());
        assertEquals(LocalTime.of(13, 0), a.getHoraFim());
        assertEquals(Integer.valueOf(60), a.getDuracaoMinutos());
        assertNotNull(a.getDataUltimaAtualizacao());
    }

    @Test
    public void inativarEAtivar_devemAlterarStatus() {
        AgendaMedica a = AgendaMedica.criar(medico, DiaSemana.SEGUNDA,
                LocalTime.of(8, 0), LocalTime.of(12, 0), 30);

        a.inativar();
        assertEquals(IndicativoStatus.I, a.getStatus());
        assertFalse(a.isAtiva());

        a.ativar();
        assertEquals(IndicativoStatus.A, a.getStatus());
        assertTrue(a.isAtiva());
    }
}
