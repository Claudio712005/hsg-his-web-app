package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.StatusEnvioConvite;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class EnvioConviteHistoricoTest {

    @Test
    public void registrarEnvio_deveDefinirStatusEnviado() {
        EnvioConviteHistorico h = EnvioConviteHistorico.registrarEnvio(
                mock(PreCadastroProfissional.class), 1L, "Admin", LocalDateTime.now().plusDays(2));
        assertEquals(StatusEnvioConvite.ENVIADO, h.getStatus());
        assertFalse(h.isExpirado());
        assertEquals("ativo", h.getStatusCssClass());
    }

    @Test
    public void registrarErro_deveDefinirStatusErroComMensagem() {
        EnvioConviteHistorico h = EnvioConviteHistorico.registrarErro(
                mock(PreCadastroProfissional.class), 1L, "Admin", LocalDateTime.now().plusDays(2), "smtp down");
        assertEquals(StatusEnvioConvite.ERRO, h.getStatus());
        assertEquals("smtp down", h.getMensagemErro());
        assertEquals("inativo", h.getStatusCssClass());
    }

    @Test
    public void isExpirado_deveSerTrueQuandoExpiracaoNoPassado() {
        EnvioConviteHistorico h = EnvioConviteHistorico.registrarEnvio(
                mock(PreCadastroProfissional.class), 1L, "Admin", LocalDateTime.now().minusDays(1));
        assertTrue(h.isExpirado());
    }

    @Test
    public void marcarAceito_deveAlterarStatus() {
        EnvioConviteHistorico h = EnvioConviteHistorico.registrarEnvio(
                mock(PreCadastroProfissional.class), 1L, "Admin", LocalDateTime.now().plusDays(2));
        h.marcarAceito();
        assertEquals(StatusEnvioConvite.ACEITO, h.getStatus());
    }
}
