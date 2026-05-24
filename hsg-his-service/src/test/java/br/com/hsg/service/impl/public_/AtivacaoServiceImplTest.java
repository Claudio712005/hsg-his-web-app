package br.com.hsg.service.impl.public_;

import br.com.hsg.dao.ContaUsuarioDAO;
import br.com.hsg.dao.EnfermeiroDAO;
import br.com.hsg.dao.EspecialidadeDAO;
import br.com.hsg.dao.MedicoDAO;
import br.com.hsg.dao.PreCadastroProfissionalDAO;
import br.com.hsg.domain.entity.PreCadastroProfissional;
import br.com.hsg.service.crypto.CpfCryptoService;
import br.com.hsg.service.dto.AtivacaoFormDTO;
import br.com.hsg.service.keycloak.KeycloakAdminService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AtivacaoServiceImplTest {

    @Mock private PreCadastroProfissionalDAO preCadastroDAO;
    @Mock private MedicoDAO medicoDAO;
    @Mock private EnfermeiroDAO enfermeiroDAO;
    @Mock private EspecialidadeDAO especialidadeDAO;
    @Mock private ContaUsuarioDAO contaUsuarioDAO;
    @Mock private KeycloakAdminService keycloakService;
    @Mock private CpfCryptoService cpfCrypto;

    @InjectMocks private AtivacaoServiceImpl service;

    private AtivacaoFormDTO formValido() {
        AtivacaoFormDTO form = new AtivacaoFormDTO();
        Date nasc = Date.from(LocalDate.now().minusYears(30)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
        form.setDataNascimento(nasc);
        form.setTelefone("11912345678");
        form.setSenha("Senha@123");
        form.setConfirmacaoSenha("Senha@123");
        return form;
    }

    private PreCadastroProfissional prePendente() {
        PreCadastroProfissional pre = mock(PreCadastroProfissional.class);
        when(pre.isPendente()).thenReturn(true);
        when(pre.isConviteExpirado()).thenReturn(false);
        when(pre.getEmailCorporativo()).thenReturn("joao.silva@hsg.com.br");
        when(pre.getNome()).thenReturn("Joao Silva");
        return pre;
    }

    @Test
    public void validarToken_deveRetornarNuloSeTokenVazio() {
        assertNull(service.validarToken("  "));
    }

    @Test
    public void validarToken_deveDelegarAoDAO() {
        PreCadastroProfissional pre = mock(PreCadastroProfissional.class);
        when(preCadastroDAO.buscarPorToken("tok")).thenReturn(pre);
        assertSame(pre, service.validarToken("tok"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ativarCadastro_deveLancarExcecaoSeFormNulo() {
        service.ativarCadastro("tok", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ativarCadastro_deveLancarExcecaoSeSenhaCurta() {
        AtivacaoFormDTO form = formValido();
        form.setSenha("Ab@1");
        form.setConfirmacaoSenha("Ab@1");
        service.ativarCadastro("tok", form);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ativarCadastro_deveLancarExcecaoSeTelefoneInvalido() {
        AtivacaoFormDTO form = formValido();
        form.setTelefone("123");
        service.ativarCadastro("tok", form);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ativarCadastro_deveLancarExcecaoSeMenorDeIdade() {
        AtivacaoFormDTO form = formValido();
        form.setDataNascimento(Date.from(LocalDate.now().minusYears(10)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        service.ativarCadastro("tok", form);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ativarCadastro_deveLancarExcecaoSeSenhasNaoCoincidem() {
        AtivacaoFormDTO form = formValido();
        form.setConfirmacaoSenha("Outra@123");
        service.ativarCadastro("tok", form);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ativarCadastro_deveLancarExcecaoSeTokenNaoEncontrado() {
        when(preCadastroDAO.buscarPorToken("tok")).thenReturn(null);
        service.ativarCadastro("tok", formValido());
    }

    @Test(expected = IllegalStateException.class)
    public void ativarCadastro_deveLancarExcecaoSeNaoPendente() {
        PreCadastroProfissional pre = mock(PreCadastroProfissional.class);
        when(pre.isPendente()).thenReturn(false);
        when(preCadastroDAO.buscarPorToken("tok")).thenReturn(pre);
        service.ativarCadastro("tok", formValido());
    }

    @Test(expected = IllegalStateException.class)
    public void ativarCadastro_deveLancarExcecaoSeConviteExpirado() {
        PreCadastroProfissional pre = mock(PreCadastroProfissional.class);
        when(pre.isPendente()).thenReturn(true);
        when(pre.isConviteExpirado()).thenReturn(true);
        when(preCadastroDAO.buscarPorToken("tok")).thenReturn(pre);
        service.ativarCadastro("tok", formValido());
    }

    @Test
    public void ativarCadastro_deveCompensarKeycloakSeConfiguracaoFalhar() {
        PreCadastroProfissional pre = prePendente();
        when(preCadastroDAO.buscarPorToken("tok")).thenReturn(pre);
        when(keycloakService.criarUsuario(any(), any(), any(), any())).thenReturn("kc1");
        doThrow(new RuntimeException("kc")).when(keycloakService).definirSenha("kc1", "Senha@123");

        try {
            service.ativarCadastro("tok", formValido());
        } catch (RuntimeException expected) {
            verify(keycloakService).tentarRemoverUsuario("kc1");
            return;
        }
        org.junit.Assert.fail("Esperava RuntimeException");
    }
}
