package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.StatusSolicitacao;
import br.com.hsg.domain.enums.TipoTitularidade;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class SolicitacaoConvenioTest {

    private SolicitacaoConvenio pendente() {
        return SolicitacaoConvenio.solicitar(mock(Paciente.class), mock(PlanoConvenio.class),
                "enc", "****", null, TipoTitularidade.TITULAR, "motivo", null);
    }

    @Test
    public void solicitar_deveIniciarPendenteComTitularidadePadrao() {
        SolicitacaoConvenio s = SolicitacaoConvenio.solicitar(mock(Paciente.class), mock(PlanoConvenio.class),
                "enc", "****", null, null, "motivo", null);
        assertEquals(StatusSolicitacao.P, s.getStatus());
        assertEquals(TipoTitularidade.TITULAR, s.getTipoTitularidade());
    }

    @Test(expected = IllegalArgumentException.class)
    public void solicitar_deveLancarExcecaoSePacienteNulo() {
        SolicitacaoConvenio.solicitar(null, mock(PlanoConvenio.class), "enc", "****",
                null, TipoTitularidade.TITULAR, "m", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void solicitar_deveLancarExcecaoSeValidadePassada() {
        SolicitacaoConvenio.solicitar(mock(Paciente.class), mock(PlanoConvenio.class), "enc", "****",
                LocalDate.now().minusDays(1), TipoTitularidade.TITULAR, "m", null);
    }

    @Test
    public void aprovar_deveMudarStatusParaAprovada() {
        SolicitacaoConvenio s = pendente();
        s.aprovar(99L);
        assertEquals(StatusSolicitacao.A, s.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void aprovar_deveLancarExcecaoSeNaoPendente() {
        SolicitacaoConvenio s = pendente();
        s.aprovar(99L);
        s.aprovar(99L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejeitar_deveLancarExcecaoSeMotivoVazio() {
        pendente().rejeitar(99L, "  ");
    }

    @Test
    public void cancelar_deveMudarStatusParaCancelada() {
        SolicitacaoConvenio s = pendente();
        s.cancelar();
        assertEquals(StatusSolicitacao.C, s.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void cancelar_deveLancarExcecaoSeNaoPendente() {
        SolicitacaoConvenio s = pendente();
        s.cancelar();
        s.cancelar();
    }
}
