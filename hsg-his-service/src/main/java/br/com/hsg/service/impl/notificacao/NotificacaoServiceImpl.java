package br.com.hsg.service.impl.notificacao;

import br.com.hsg.dao.NotificacaoDAO;
import br.com.hsg.domain.entity.Notificacao;
import br.com.hsg.domain.enums.CategoriaNotificacao;
import br.com.hsg.domain.enums.TipoDestinatarioNotificacao;
import br.com.hsg.domain.enums.TipoNotificacao;
import br.com.hsg.service.facade.notificacao.NotificacaoServiceFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class NotificacaoServiceImpl implements NotificacaoServiceFacade {

    private static final Logger LOG = Logger.getLogger(NotificacaoServiceImpl.class.getName());

    @EJB private NotificacaoDAO notificacaoDAO;

    @Override
    public Notificacao notificar(TipoDestinatarioNotificacao tipoDestinatario, Long idDestinatario,
                                  String titulo, String mensagem,
                                  TipoNotificacao tipo, CategoriaNotificacao categoria, String link) {
        Notificacao n = Notificacao.criar(tipoDestinatario, idDestinatario,
                titulo, mensagem, tipo, categoria, link);
        Notificacao salva = notificacaoDAO.salvar(n);
        LOG.log(Level.INFO, "[NotificacaoServiceImpl] Notificação criada: destinatario={0}#{1}, categoria={2}, tipo={3}",
                new Object[]{tipoDestinatario, idDestinatario, categoria, tipo});
        return salva;
    }

    @Override
    public List<Notificacao> listar(TipoDestinatarioNotificacao tipoDestinatario, Long idDestinatario,
                                     int primeiro, int tamanho) {
        if (tipoDestinatario == null || idDestinatario == null) {
            return java.util.Collections.emptyList();
        }
        int t = (tamanho <= 0 || tamanho > 200) ? 50 : tamanho;
        int p = Math.max(primeiro, 0);
        return notificacaoDAO.listarPorDestinatario(tipoDestinatario, idDestinatario, p, t);
    }

    @Override
    public List<Notificacao> listarFiltrado(TipoDestinatarioNotificacao tipoDestinatario, Long idDestinatario,
                                             Boolean lidaFiltro, String termoBusca,
                                             int primeiro, int tamanho) {
        if (tipoDestinatario == null || idDestinatario == null) {
            return java.util.Collections.emptyList();
        }
        int t = (tamanho <= 0 || tamanho > 200) ? 50 : tamanho;
        int p = Math.max(primeiro, 0);
        return notificacaoDAO.listarFiltrado(tipoDestinatario, idDestinatario,
                lidaFiltro, termoBusca, p, t);
    }

    @Override
    public long contarNaoLidas(TipoDestinatarioNotificacao tipoDestinatario, Long idDestinatario) {
        if (tipoDestinatario == null || idDestinatario == null) return 0L;
        return notificacaoDAO.contarNaoLidas(tipoDestinatario, idDestinatario);
    }

    @Override
    public void marcarComoLida(Long idNotificacao, TipoDestinatarioNotificacao tipoDestinatario,
                                Long idDestinatario) {
        Notificacao n = notificacaoDAO.buscarPorId(idNotificacao);
        if (n == null) {
            throw new IllegalArgumentException("Notificação não encontrada.");
        }
        if (n.getTipoDestinatario() != tipoDestinatario || !n.getIdDestinatario().equals(idDestinatario)) {
            throw new IllegalStateException("Esta notificação não pertence ao destinatário informado.");
        }
        n.marcarComoLida();
        notificacaoDAO.atualizar(n);
    }

    @Override
    public int marcarTodasComoLidas(TipoDestinatarioNotificacao tipoDestinatario, Long idDestinatario) {
        if (tipoDestinatario == null || idDestinatario == null) return 0;
        return notificacaoDAO.marcarTodasComoLidas(tipoDestinatario, idDestinatario, LocalDateTime.now());
    }

    @Override
    public int limparExpiradas() {
        int n = notificacaoDAO.removerExpiradas(LocalDateTime.now());
        LOG.info("[NotificacaoServiceImpl] Notificações expiradas removidas: " + n);
        return n;
    }
}
