package br.com.hsg.service.impl.notificacao;

import br.com.hsg.dao.NotificacaoDAO;
import br.com.hsg.domain.entity.Notificacao;
import br.com.hsg.domain.enums.CategoriaNotificacao;
import br.com.hsg.domain.enums.TipoDestinatarioNotificacao;
import br.com.hsg.domain.enums.TipoNotificacao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class NotificacaoServiceImplTest {

    @Mock private NotificacaoDAO notificacaoDAO;
    @InjectMocks private NotificacaoServiceImpl service;

    @Test
    public void notificar_deveCriarESalvar() {
        when(notificacaoDAO.salvar(any(Notificacao.class))).thenAnswer(i -> i.getArgument(0));

        Notificacao n = service.notificar(TipoDestinatarioNotificacao.PACIENTE, 10L,
                "Título", "Mensagem", TipoNotificacao.SUCESSO, CategoriaNotificacao.CONSULTA,
                "/paciente/x.xhtml");

        assertNotNull(n);
        verify(notificacaoDAO).salvar(any(Notificacao.class));
    }

    @Test
    public void listar_paramsInvalidos_retornaVazio() {
        assertEquals(0, service.listar(null, 1L, 0, 10).size());
        assertEquals(0, service.listar(TipoDestinatarioNotificacao.ADMIN, null, 0, 10).size());
    }

    @Test
    public void listar_aplicaLimiteDefaultQuandoTamanhoInvalido() {
        service.listar(TipoDestinatarioNotificacao.PACIENTE, 1L, -1, 0);
        verify(notificacaoDAO).listarPorDestinatario(eq(TipoDestinatarioNotificacao.PACIENTE), eq(1L), eq(0), eq(50));
    }

    @Test
    public void marcarComoLida_lancaQuandoNaoEncontrada() {
        when(notificacaoDAO.buscarPorId(99L)).thenReturn(null);
        try {
            service.marcarComoLida(99L, TipoDestinatarioNotificacao.PACIENTE, 10L);
            org.junit.Assert.fail();
        } catch (IllegalArgumentException ok) { }
    }

    @Test
    public void marcarComoLida_lancaQuandoDestinatarioDivergente() {
        Notificacao n = mock(Notificacao.class);
        when(n.getTipoDestinatario()).thenReturn(TipoDestinatarioNotificacao.MEDICO);
        when(n.getIdDestinatario()).thenReturn(5L);
        when(notificacaoDAO.buscarPorId(1L)).thenReturn(n);

        try {
            service.marcarComoLida(1L, TipoDestinatarioNotificacao.PACIENTE, 10L);
            org.junit.Assert.fail();
        } catch (IllegalStateException ok) { }
    }

    @Test
    public void marcarComoLida_delegaParaEntidade() {
        Notificacao n = mock(Notificacao.class);
        when(n.getTipoDestinatario()).thenReturn(TipoDestinatarioNotificacao.PACIENTE);
        when(n.getIdDestinatario()).thenReturn(10L);
        when(notificacaoDAO.buscarPorId(1L)).thenReturn(n);

        service.marcarComoLida(1L, TipoDestinatarioNotificacao.PACIENTE, 10L);

        verify(n).marcarComoLida();
        verify(notificacaoDAO).atualizar(n);
    }

    @Test
    public void listarFiltrado_paramsInvalidos_retornaVazio() {
        assertEquals(0, service.listarFiltrado(null, 1L, Boolean.TRUE, null, 0, 10).size());
        assertEquals(0, service.listarFiltrado(TipoDestinatarioNotificacao.ADMIN, null, null, "x", 0, 10).size());
    }

    @Test
    public void listarFiltrado_delegaPassandoFiltros() {
        service.listarFiltrado(TipoDestinatarioNotificacao.PACIENTE, 10L,
                Boolean.FALSE, "consulta", 0, 25);
        verify(notificacaoDAO).listarFiltrado(
                eq(TipoDestinatarioNotificacao.PACIENTE), eq(10L),
                eq(Boolean.FALSE), eq("consulta"), eq(0), eq(25));
    }

    @Test
    public void marcarTodasComoLidas_delegaAoDao() {
        when(notificacaoDAO.marcarTodasComoLidas(eq(TipoDestinatarioNotificacao.PACIENTE), eq(10L), any()))
                .thenReturn(3);
        int n = service.marcarTodasComoLidas(TipoDestinatarioNotificacao.PACIENTE, 10L);
        assertEquals(3, n);
    }

    @Test
    public void limparExpiradas_delegaAoDao() {
        when(notificacaoDAO.removerExpiradas(any())).thenReturn(7);
        assertEquals(7, service.limparExpiradas());
    }
}
