package br.com.hsg.service.impl.paciente;

import br.com.hsg.dao.EnderecoDAO;
import br.com.hsg.dao.PacienteDAO;
import br.com.hsg.dao.PainelPacienteDAO;
import br.com.hsg.dao.SolicitacaoAtualizacaoDAO;
import br.com.hsg.domain.entity.SolicitacaoAtualizacao;
import br.com.hsg.domain.enums.TipoCancelador;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SolicitacaoAtualizacaoServiceImplTest {

    @Mock private SolicitacaoAtualizacaoDAO solicitacaoDAO;
    @Mock private PacienteDAO pacienteDAO;
    @Mock private EnderecoDAO enderecoDAO;
    @Mock private PainelPacienteDAO painelDAO;

    @InjectMocks private SolicitacaoAtualizacaoServiceImpl service;

    @Test(expected = IllegalArgumentException.class)
    public void solicitarAtualizacaoCadastral_deveLancarExcecaoSePacienteNaoEncontrado() {
        when(pacienteDAO.buscarPorId(1L)).thenReturn(null);
        service.solicitarAtualizacaoCadastral(1L, "Joao", "Silva", "joao@gmail.com", "11912345678", "motivo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void solicitarAtualizacaoEndereco_deveLancarExcecaoSePacienteNaoEncontrado() {
        when(pacienteDAO.buscarPorId(1L)).thenReturn(null);
        service.solicitarAtualizacaoEndereco(1L, "Rua A", "10", null, "Centro",
                "Cidade", null, "01000000", "motivo");
    }

    @Test
    public void cancelarSolicitacao_deveCancelarEAtualizar() {
        SolicitacaoAtualizacao s = mock(SolicitacaoAtualizacao.class);
        when(solicitacaoDAO.buscarPorId(1L)).thenReturn(s);

        service.cancelarSolicitacao(1L, 9L, TipoCancelador.CLIENTE, "desisti");

        verify(s).cancelar(9L, TipoCancelador.CLIENTE, "desisti");
        verify(solicitacaoDAO).atualizar(s);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cancelarSolicitacao_deveLancarExcecaoSeNaoEncontrada() {
        when(solicitacaoDAO.buscarPorId(1L)).thenReturn(null);
        service.cancelarSolicitacao(1L, 9L, TipoCancelador.CLIENTE, "x");
    }

    @Test
    public void buscarEnderecoPorPaciente_deveDelegarAoDAO() {
        service.buscarEnderecoPorPaciente(5L);
        verify(enderecoDAO).buscarPorPaciente(5L);
    }
}
