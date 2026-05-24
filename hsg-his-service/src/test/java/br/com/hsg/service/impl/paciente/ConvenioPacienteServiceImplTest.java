package br.com.hsg.service.impl.paciente;

import br.com.hsg.dao.ConvenioDAO;
import br.com.hsg.dao.PacienteConvenioDAO;
import br.com.hsg.dao.PacienteDAO;
import br.com.hsg.dao.PlanoConvenioDAO;
import br.com.hsg.dao.RegraCoberturaDAO;
import br.com.hsg.dao.SolicitacaoConvenioDAO;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.domain.entity.PlanoConvenio;
import br.com.hsg.domain.entity.SolicitacaoConvenio;
import br.com.hsg.domain.enums.TipoTitularidade;
import br.com.hsg.service.crypto.CarteirinhaCryptoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConvenioPacienteServiceImplTest {

    @Mock private PacienteConvenioDAO pacienteConvenioDAO;
    @Mock private SolicitacaoConvenioDAO solicitacaoConvenioDAO;
    @Mock private PacienteDAO pacienteDAO;
    @Mock private PlanoConvenioDAO planoConvenioDAO;
    @Mock private ConvenioDAO convenioDAO;
    @Mock private RegraCoberturaDAO regraCoberturaDAO;
    @Mock private CarteirinhaCryptoService carteirinhaCrypto;

    @InjectMocks private ConvenioPacienteServiceImpl service;

    @Test
    public void solicitarConvenio_deveSalvarSolicitacao() {
        Paciente paciente = mock(Paciente.class);
        PlanoConvenio plano = mock(PlanoConvenio.class);
        when(pacienteDAO.buscarPorId(1L)).thenReturn(paciente);
        when(planoConvenioDAO.buscarPorId(2L)).thenReturn(plano);
        when(plano.isAtivo()).thenReturn(true);
        when(solicitacaoConvenioDAO.existePendentePorPaciente(1L)).thenReturn(false);
        when(carteirinhaCrypto.encrypt("123")).thenReturn("enc");
        when(carteirinhaCrypto.mascarar("123")).thenReturn("****");
        when(pacienteConvenioDAO.buscarAtivoPorPaciente(1L)).thenReturn(null);

        service.solicitarConvenio(1L, 2L, "123", null, TipoTitularidade.TITULAR, "motivo");

        verify(solicitacaoConvenioDAO).salvar(any(SolicitacaoConvenio.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void solicitarConvenio_deveLancarExcecaoSePacienteNaoEncontrado() {
        when(pacienteDAO.buscarPorId(1L)).thenReturn(null);
        service.solicitarConvenio(1L, 2L, "123", null, TipoTitularidade.TITULAR, "m");
    }

    @Test(expected = IllegalArgumentException.class)
    public void solicitarConvenio_deveLancarExcecaoSePlanoNaoEncontrado() {
        when(pacienteDAO.buscarPorId(1L)).thenReturn(mock(Paciente.class));
        when(planoConvenioDAO.buscarPorId(2L)).thenReturn(null);
        service.solicitarConvenio(1L, 2L, "123", null, TipoTitularidade.TITULAR, "m");
    }

    @Test(expected = IllegalStateException.class)
    public void solicitarConvenio_deveLancarExcecaoSePlanoInativo() {
        PlanoConvenio plano = mock(PlanoConvenio.class);
        when(pacienteDAO.buscarPorId(1L)).thenReturn(mock(Paciente.class));
        when(planoConvenioDAO.buscarPorId(2L)).thenReturn(plano);
        when(plano.isAtivo()).thenReturn(false);
        service.solicitarConvenio(1L, 2L, "123", null, TipoTitularidade.TITULAR, "m");
    }

    @Test(expected = IllegalArgumentException.class)
    public void solicitarConvenio_deveLancarExcecaoSeCarteirinhaVazia() {
        PlanoConvenio plano = mock(PlanoConvenio.class);
        when(pacienteDAO.buscarPorId(1L)).thenReturn(mock(Paciente.class));
        when(planoConvenioDAO.buscarPorId(2L)).thenReturn(plano);
        when(plano.isAtivo()).thenReturn(true);
        service.solicitarConvenio(1L, 2L, "  ", null, TipoTitularidade.TITULAR, "m");
    }

    @Test(expected = IllegalStateException.class)
    public void solicitarConvenio_deveLancarExcecaoSeJaExistePendente() {
        PlanoConvenio plano = mock(PlanoConvenio.class);
        when(pacienteDAO.buscarPorId(1L)).thenReturn(mock(Paciente.class));
        when(planoConvenioDAO.buscarPorId(2L)).thenReturn(plano);
        when(plano.isAtivo()).thenReturn(true);
        when(solicitacaoConvenioDAO.existePendentePorPaciente(1L)).thenReturn(true);
        service.solicitarConvenio(1L, 2L, "123", null, TipoTitularidade.TITULAR, "m");
    }

    @Test
    public void cancelarSolicitacao_deveCancelarQuandoPertenceAoPaciente() {
        SolicitacaoConvenio s = mock(SolicitacaoConvenio.class);
        Paciente paciente = mock(Paciente.class);
        when(solicitacaoConvenioDAO.buscarPorId(1L)).thenReturn(s);
        when(s.getPaciente()).thenReturn(paciente);
        when(paciente.getId()).thenReturn(7L);

        service.cancelarSolicitacao(1L, 7L);

        verify(s).cancelar();
        verify(solicitacaoConvenioDAO).atualizar(s);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cancelarSolicitacao_deveLancarExcecaoSeNaoEncontrada() {
        when(solicitacaoConvenioDAO.buscarPorId(1L)).thenReturn(null);
        service.cancelarSolicitacao(1L, 7L);
    }

    @Test(expected = IllegalStateException.class)
    public void cancelarSolicitacao_deveLancarExcecaoSeNaoPertenceAoPaciente() {
        SolicitacaoConvenio s = mock(SolicitacaoConvenio.class);
        Paciente paciente = mock(Paciente.class);
        when(solicitacaoConvenioDAO.buscarPorId(1L)).thenReturn(s);
        when(s.getPaciente()).thenReturn(paciente);
        when(paciente.getId()).thenReturn(99L);
        service.cancelarSolicitacao(1L, 7L);
    }
}
