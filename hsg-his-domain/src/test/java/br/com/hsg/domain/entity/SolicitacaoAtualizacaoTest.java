package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.StatusSolicitacao;
import br.com.hsg.domain.enums.TipoCancelador;
import br.com.hsg.domain.enums.TipoSolicitacao;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class SolicitacaoAtualizacaoTest {

    private SolicitacaoAtualizacao clinicaPendente() {
        return SolicitacaoAtualizacao.solicitarClinica(mock(Paciente.class),
                70.0, 1.70, "A+", 72.0, 1.71, "B+", "motivo");
    }

    @Test
    public void solicitarClinica_deveIniciarPendente() {
        SolicitacaoAtualizacao s = clinicaPendente();
        assertEquals(StatusSolicitacao.P, s.getStatus());
        assertEquals(TipoSolicitacao.CLINICO, s.getTipoSolicitacao());
    }

    @Test(expected = IllegalArgumentException.class)
    public void solicitarClinica_deveLancarExcecaoSePesoInvalido() {
        SolicitacaoAtualizacao.solicitarClinica(mock(Paciente.class),
                null, null, null, -1.0, 1.71, "B+", "motivo");
    }

    @Test
    public void cancelar_deveMudarStatusParaCancelada() {
        SolicitacaoAtualizacao s = clinicaPendente();
        s.cancelar(9L, TipoCancelador.CLIENTE, "desisti");
        assertEquals(StatusSolicitacao.C, s.getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cancelar_deveLancarExcecaoSeMotivoVazio() {
        clinicaPendente().cancelar(9L, TipoCancelador.CLIENTE, " ");
    }

    @Test(expected = IllegalStateException.class)
    public void cancelar_deveLancarExcecaoSeNaoPendente() {
        SolicitacaoAtualizacao s = clinicaPendente();
        s.cancelar(9L, TipoCancelador.CLIENTE, "desisti");
        s.cancelar(9L, TipoCancelador.CLIENTE, "denovo");
    }
}
