package br.com.hsg.service.impl.admin;

import br.com.hsg.dao.EnvioConviteHistoricoDAO;
import br.com.hsg.dao.PreCadastroProfissionalDAO;
import br.com.hsg.domain.entity.PreCadastroProfissional;
import br.com.hsg.domain.enums.TipoProfissional;
import br.com.hsg.service.email.EmailCorporativoService;
import br.com.hsg.service.mail.MailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PreCadastroServiceImplTest {

    @Mock private PreCadastroProfissionalDAO preCadastroDAO;
    @Mock private EnvioConviteHistoricoDAO historicoDAO;
    @Mock private MailService mailService;
    @Mock private EmailCorporativoService emailCorporativoService;

    @InjectMocks private PreCadastroServiceImpl service;

    @Test
    public void criarParaMedico_deveSalvarEDefinirEmailCorporativo() {
        PreCadastroProfissional p = mock(PreCadastroProfissional.class);
        when(preCadastroDAO.existeEmailPendente(anyString())).thenReturn(false);
        when(preCadastroDAO.existeCpfPendente(anyString())).thenReturn(false);
        when(preCadastroDAO.salvar(any(PreCadastroProfissional.class))).thenReturn(p);
        when(emailCorporativoService.gerar(anyString())).thenReturn("joao.silva@hsg.com.br");
        when(preCadastroDAO.atualizar(p)).thenReturn(p);

        PreCadastroProfissional resultado = service.criarParaMedico(
                "Joao Silva", "joao@gmail.com", "12345678900", "1234", "SP", "Cardiologia", 1L);

        assertSame(p, resultado);
        verify(p).definirEmailCorporativo("joao.silva@hsg.com.br");
        verify(preCadastroDAO).atualizar(p);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criarParaMedico_deveLancarExcecaoSeNomeVazio() {
        service.criarParaMedico("  ", "joao@gmail.com", "12345678900", "1234", "SP", "Cardiologia", 1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criarParaMedico_deveLancarExcecaoSeEmailInvalido() {
        service.criarParaMedico("Joao", "joaogmail.com", "12345678900", "1234", "SP", "Cardiologia", 1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criarParaMedico_deveLancarExcecaoSeCrmVazio() {
        service.criarParaMedico("Joao", "joao@gmail.com", "12345678900", " ", "SP", "Cardiologia", 1L);
    }

    @Test(expected = IllegalStateException.class)
    public void criarParaMedico_deveLancarExcecaoSeEmailJaPendente() {
        when(preCadastroDAO.existeEmailPendente("joao@gmail.com")).thenReturn(true);
        service.criarParaMedico("Joao", "joao@gmail.com", "12345678900", "1234", "SP", "Cardiologia", 1L);
    }

    @Test(expected = IllegalStateException.class)
    public void criarParaMedico_deveLancarExcecaoSeCpfJaPendente() {
        when(preCadastroDAO.existeEmailPendente(anyString())).thenReturn(false);
        when(preCadastroDAO.existeCpfPendente("12345678900")).thenReturn(true);
        service.criarParaMedico("Joao", "joao@gmail.com", "12345678900", "1234", "SP", "Cardiologia", 1L);
    }

    @Test
    public void enviarConvite_deveRegistrarEnvioQuandoPendente() {
        PreCadastroProfissional p = mock(PreCadastroProfissional.class);
        when(preCadastroDAO.buscarPorId(1L)).thenReturn(p);
        when(p.isPendente()).thenReturn(true);
        when(p.isEmailEnviado()).thenReturn(false);
        when(p.getTipoProfissional()).thenReturn(TipoProfissional.MEDICO);

        service.enviarConvite(1L, 9L, "Admin");

        verify(p).registrarEnvioEmail(anyInt());
        verify(mailService).enviarConviteProfissional(any(), any(), any(), any(), any());
        verify(historicoDAO).salvar(any());
    }

    @Test(expected = IllegalStateException.class)
    public void enviarConvite_deveLancarExcecaoSeNaoPendente() {
        PreCadastroProfissional p = mock(PreCadastroProfissional.class);
        when(preCadastroDAO.buscarPorId(1L)).thenReturn(p);
        when(p.isPendente()).thenReturn(false);
        service.enviarConvite(1L, 9L, "Admin");
    }

    @Test(expected = IllegalStateException.class)
    public void enviarConvite_deveLancarExcecaoSeConviteAnteriorAindaValido() {
        PreCadastroProfissional p = mock(PreCadastroProfissional.class);
        when(preCadastroDAO.buscarPorId(1L)).thenReturn(p);
        when(p.isPendente()).thenReturn(true);
        when(p.isEmailEnviado()).thenReturn(true);
        when(p.isConviteExpirado()).thenReturn(false);
        service.enviarConvite(1L, 9L, "Admin");
    }

    @Test(expected = RuntimeException.class)
    public void enviarConvite_deveRegistrarErroEPropagardSeMailFalhar() {
        PreCadastroProfissional p = mock(PreCadastroProfissional.class);
        when(preCadastroDAO.buscarPorId(1L)).thenReturn(p);
        when(p.isPendente()).thenReturn(true);
        when(p.isEmailEnviado()).thenReturn(false);
        when(p.getTipoProfissional()).thenReturn(TipoProfissional.MEDICO);
        doThrow(new RuntimeException("smtp")).when(mailService)
                .enviarConviteProfissional(any(), any(), any(), any(), any());
        try {
            service.enviarConvite(1L, 9L, "Admin");
        } finally {
            verify(historicoDAO).salvar(any());
        }
    }

    @Test
    public void concluirCadastro_deveConcluirQuandoTokenValido() {
        PreCadastroProfissional p = mock(PreCadastroProfissional.class);
        when(preCadastroDAO.buscarPorToken("tok")).thenReturn(p);
        when(p.isPendente()).thenReturn(true);
        when(p.isConviteExpirado()).thenReturn(false);

        service.concluirCadastro("tok");

        verify(p).concluir();
        verify(preCadastroDAO).atualizar(p);
    }

    @Test(expected = IllegalArgumentException.class)
    public void concluirCadastro_deveLancarExcecaoSeTokenVazio() {
        service.concluirCadastro("  ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void concluirCadastro_deveLancarExcecaoSeTokenNaoEncontrado() {
        when(preCadastroDAO.buscarPorToken("tok")).thenReturn(null);
        service.concluirCadastro("tok");
    }

    @Test(expected = IllegalStateException.class)
    public void concluirCadastro_deveLancarExcecaoSeNaoPendente() {
        PreCadastroProfissional p = mock(PreCadastroProfissional.class);
        when(preCadastroDAO.buscarPorToken("tok")).thenReturn(p);
        when(p.isPendente()).thenReturn(false);
        service.concluirCadastro("tok");
    }

    @Test(expected = IllegalStateException.class)
    public void concluirCadastro_deveLancarExcecaoSeConviteExpirado() {
        PreCadastroProfissional p = mock(PreCadastroProfissional.class);
        when(preCadastroDAO.buscarPorToken("tok")).thenReturn(p);
        when(p.isPendente()).thenReturn(true);
        when(p.isConviteExpirado()).thenReturn(true);
        service.concluirCadastro("tok");
    }
}
