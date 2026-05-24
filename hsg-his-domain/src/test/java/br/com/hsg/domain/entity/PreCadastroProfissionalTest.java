package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.CategoriaCoren;
import br.com.hsg.domain.enums.StatusPreCadastro;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class PreCadastroProfissionalTest {

    @Test
    public void criarParaMedico_deveIniciarPendenteComToken() {
        PreCadastroProfissional p = PreCadastroProfissional.criarParaMedico(
                "Joao Silva", "joao@gmail.com", "12345678900", "1234", "SP", "Cardiologia", 1L);
        assertTrue(p.isMedico());
        assertFalse(p.isEnfermeiro());
        assertTrue(p.isPendente());
        assertNotNull(p.getTokenConvite());
        assertEquals("CRM-SP 1234", p.getCrmFormatado());
    }

    @Test
    public void criarParaEnfermeiro_deveDefinirDadosCoren() {
        PreCadastroProfissional p = PreCadastroProfissional.criarParaEnfermeiro(
                "Maria Souza", "maria@gmail.com", "12345678900", "9876", "RJ", CategoriaCoren.ENF, 1L);
        assertTrue(p.isEnfermeiro());
        assertEquals("COREN-RJ 9876/ENF", p.getCorenFormatado());
    }

    @Test
    public void registrarEnvioEmail_deveAtualizarTokenEContador() {
        PreCadastroProfissional p = PreCadastroProfissional.criarParaMedico(
                "Joao", "joao@gmail.com", "12345678900", "1234", "SP", "Cardio", 1L);
        String tokenAntes = p.getTokenConvite();

        LocalDateTime expiracao = p.registrarEnvioEmail(2);

        assertTrue(p.isEmailEnviado());
        assertEquals(1, p.getQuantidadeEnvios());
        assertNotEquals(tokenAntes, p.getTokenConvite());
        assertTrue(expiracao.isAfter(LocalDateTime.now()));
        assertFalse(p.isConviteExpirado());
    }

    @Test
    public void concluir_deveAlterarStatusParaConcluido() {
        PreCadastroProfissional p = PreCadastroProfissional.criarParaMedico(
                "Joao", "joao@gmail.com", "12345678900", "1234", "SP", "Cardio", 1L);
        p.concluir();
        assertEquals(StatusPreCadastro.CONCLUIDO, p.getStatus());
        assertFalse(p.isPendente());
    }

    @Test
    public void expirar_deveAlterarStatusParaExpirado() {
        PreCadastroProfissional p = PreCadastroProfissional.criarParaMedico(
                "Joao", "joao@gmail.com", "12345678900", "1234", "SP", "Cardio", 1L);
        p.expirar();
        assertEquals(StatusPreCadastro.EXPIRADO, p.getStatus());
    }
}
