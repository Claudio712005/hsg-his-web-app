package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.CategoriaNotificacao;
import br.com.hsg.domain.enums.TipoDestinatarioNotificacao;
import br.com.hsg.domain.enums.TipoNotificacao;
import org.junit.Test;

import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;

public class NotificacaoTest {

    @Test
    public void criar_deveDefinirCamposComExpiracaoDe40Dias() {
        Notificacao n = Notificacao.criar(
                TipoDestinatarioNotificacao.PACIENTE, 10L,
                "Título", "Mensagem",
                TipoNotificacao.INFO, CategoriaNotificacao.CONSULTA, "/paciente/x.xhtml");

        assertEquals(TipoDestinatarioNotificacao.PACIENTE, n.getTipoDestinatario());
        assertEquals(Long.valueOf(10L), n.getIdDestinatario());
        assertEquals("Título", n.getTitulo());
        assertEquals("Mensagem", n.getMensagem());
        assertEquals(TipoNotificacao.INFO, n.getTipo());
        assertEquals(CategoriaNotificacao.CONSULTA, n.getCategoria());
        assertEquals("/paciente/x.xhtml", n.getLink());
        assertFalse(n.isLida());
        assertNull(n.getDataLeitura());

        long dias = ChronoUnit.DAYS.between(n.getDataCriacao(), n.getDataExpiracao());
        assertEquals(Notificacao.DIAS_RETENCAO, dias);
    }

    @Test
    public void criar_linkVazio_deveTornarseNulo() {
        Notificacao n = Notificacao.criar(
                TipoDestinatarioNotificacao.ADMIN, 1L, "t", "m",
                TipoNotificacao.INFO, CategoriaNotificacao.SISTEMA, "   ");
        assertNull(n.getLink());
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarSemDestinatarioTipo() {
        Notificacao.criar(null, 1L, "t", "m",
                TipoNotificacao.INFO, CategoriaNotificacao.SISTEMA, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarSemIdDestinatario() {
        Notificacao.criar(TipoDestinatarioNotificacao.ADMIN, null, "t", "m",
                TipoNotificacao.INFO, CategoriaNotificacao.SISTEMA, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarSemTitulo() {
        Notificacao.criar(TipoDestinatarioNotificacao.ADMIN, 1L, "  ", "m",
                TipoNotificacao.INFO, CategoriaNotificacao.SISTEMA, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarSemMensagem() {
        Notificacao.criar(TipoDestinatarioNotificacao.ADMIN, 1L, "t", "  ",
                TipoNotificacao.INFO, CategoriaNotificacao.SISTEMA, null);
    }

    @Test
    public void marcarComoLida_setaFlagEData() {
        Notificacao n = Notificacao.criar(TipoDestinatarioNotificacao.PACIENTE, 1L,
                "t", "m", TipoNotificacao.INFO, CategoriaNotificacao.CONSULTA, null);

        n.marcarComoLida();

        assertTrue(n.isLida());
        assertNotNull(n.getDataLeitura());
    }

    @Test
    public void marcarComoLida_idempotente() {
        Notificacao n = Notificacao.criar(TipoDestinatarioNotificacao.PACIENTE, 1L,
                "t", "m", TipoNotificacao.INFO, CategoriaNotificacao.CONSULTA, null);
        n.marcarComoLida();
        java.time.LocalDateTime dt = n.getDataLeitura();

        n.marcarComoLida();

        assertSame(dt, n.getDataLeitura());
    }
}
