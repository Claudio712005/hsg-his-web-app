package br.com.hsg.service.impl.admin;

import br.com.hsg.dao.PacienteConvenioDAO;
import br.com.hsg.dao.RegraCoberturaDAO;
import br.com.hsg.dao.SolicitacaoConvenioDAO;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.domain.entity.PacienteConvenio;
import br.com.hsg.domain.entity.PlanoConvenio;
import br.com.hsg.domain.entity.SolicitacaoConvenio;
import br.com.hsg.service.crypto.CarteirinhaCryptoService;
import br.com.hsg.service.mail.MailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AprovacaoConvenioServiceImplTest {

    @Mock private SolicitacaoConvenioDAO solicitacaoConvenioDAO;
    @Mock private PacienteConvenioDAO pacienteConvenioDAO;
    @Mock private RegraCoberturaDAO regraCoberturaDAO;
    @Mock private CarteirinhaCryptoService carteirinhaCrypto;
    @Mock private MailService mailService;

    @InjectMocks private AprovacaoConvenioServiceImpl service;

    private SolicitacaoConvenio solicitacaoMock(Long idPaciente) {
        SolicitacaoConvenio s = mock(SolicitacaoConvenio.class);
        Paciente paciente = mock(Paciente.class);
        PlanoConvenio plano = mock(PlanoConvenio.class);
        when(s.getPaciente()).thenReturn(paciente);
        when(s.getPlano()).thenReturn(plano);
        when(paciente.getId()).thenReturn(idPaciente);
        when(paciente.getEmail()).thenReturn(null);
        when(s.getCarteirinhaEnc()).thenReturn("enc");
        return s;
    }

    @Test
    public void aprovar_deveSalvarConvenioEAprovarSolicitacao() {
        SolicitacaoConvenio s = solicitacaoMock(5L);
        when(solicitacaoConvenioDAO.buscarPorId(1L)).thenReturn(s);
        when(pacienteConvenioDAO.buscarAtivoPorPaciente(5L)).thenReturn(null);
        when(carteirinhaCrypto.decrypt("enc")).thenReturn("12345678");
        when(carteirinhaCrypto.hash("12345678")).thenReturn("hash");

        service.aprovar(1L, 99L);

        verify(pacienteConvenioDAO).salvar(any(PacienteConvenio.class));
        verify(s).aprovar(99L);
        verify(solicitacaoConvenioDAO).atualizar(s);
    }

    @Test
    public void aprovar_deveInativarConvenioAtivoAnterior() {
        SolicitacaoConvenio s = solicitacaoMock(5L);
        PacienteConvenio ativo = mock(PacienteConvenio.class);
        when(solicitacaoConvenioDAO.buscarPorId(1L)).thenReturn(s);
        when(pacienteConvenioDAO.buscarAtivoPorPaciente(5L)).thenReturn(ativo);
        when(carteirinhaCrypto.decrypt("enc")).thenReturn("12345678");
        when(carteirinhaCrypto.hash("12345678")).thenReturn("hash");

        service.aprovar(1L, 99L);

        verify(ativo).cancelar();
        verify(pacienteConvenioDAO).atualizar(ativo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void aprovar_deveLancarExcecaoSeSolicitacaoNaoEncontrada() {
        when(solicitacaoConvenioDAO.buscarPorId(1L)).thenReturn(null);
        service.aprovar(1L, 99L);
    }

    @Test
    public void rejeitar_deveRejeitarEAtualizar() {
        SolicitacaoConvenio s = solicitacaoMock(5L);
        when(solicitacaoConvenioDAO.buscarPorId(1L)).thenReturn(s);

        service.rejeitar(1L, 99L, "Documentação inválida");

        verify(s).rejeitar(99L, "Documentação inválida");
        verify(solicitacaoConvenioDAO).atualizar(s);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejeitar_deveLancarExcecaoSeSolicitacaoNaoEncontrada() {
        when(solicitacaoConvenioDAO.buscarPorId(1L)).thenReturn(null);
        service.rejeitar(1L, 99L, "motivo");
    }
}
