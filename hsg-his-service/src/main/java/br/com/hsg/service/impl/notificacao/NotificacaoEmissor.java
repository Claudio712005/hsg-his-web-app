package br.com.hsg.service.impl.notificacao;

import br.com.hsg.dao.AdminDAO;
import br.com.hsg.domain.enums.CategoriaNotificacao;
import br.com.hsg.domain.enums.TipoDestinatarioNotificacao;
import br.com.hsg.domain.enums.TipoNotificacao;
import br.com.hsg.service.facade.notificacao.NotificacaoServiceFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class NotificacaoEmissor {

    private static final Logger LOG = Logger.getLogger(NotificacaoEmissor.class.getName());

    @EJB private NotificacaoServiceFacade notificacaoService;
    @EJB private AdminDAO adminDAO;

    public void emitir(TipoDestinatarioNotificacao tipoDestinatario, Long idDestinatario,
                       String titulo, String mensagem,
                       TipoNotificacao tipo, CategoriaNotificacao categoria, String link) {
        if (idDestinatario == null) return;
        try {
            notificacaoService.notificar(tipoDestinatario, idDestinatario, titulo, mensagem,
                    tipo, categoria, link);
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[NotificacaoEmissor] Falha ao emitir notificação", ex);
        }
    }

    public void emitirParaTodosAdmins(String titulo, String mensagem,
                                       TipoNotificacao tipo, CategoriaNotificacao categoria, String link) {
        try {
            List<Long> ids = adminDAO.listarIdsAtivos();
            for (Long idAdmin : ids) {
                emitir(TipoDestinatarioNotificacao.ADMIN, idAdmin, titulo, mensagem, tipo, categoria, link);
            }
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[NotificacaoEmissor] Falha ao listar admins ativos", ex);
        }
    }
}
