package br.com.hsg.service.facade.notificacao;

import br.com.hsg.domain.entity.Notificacao;
import br.com.hsg.domain.enums.CategoriaNotificacao;
import br.com.hsg.domain.enums.TipoDestinatarioNotificacao;
import br.com.hsg.domain.enums.TipoNotificacao;

import javax.ejb.Local;
import java.util.List;

@Local
public interface NotificacaoServiceFacade {

    Notificacao notificar(TipoDestinatarioNotificacao tipoDestinatario, Long idDestinatario,
                           String titulo, String mensagem,
                           TipoNotificacao tipo, CategoriaNotificacao categoria, String link);

    List<Notificacao> listar(TipoDestinatarioNotificacao tipoDestinatario, Long idDestinatario,
                              int primeiro, int tamanho);

    List<Notificacao> listarFiltrado(TipoDestinatarioNotificacao tipoDestinatario, Long idDestinatario,
                                      Boolean lidaFiltro, String termoBusca,
                                      int primeiro, int tamanho);

    long contarNaoLidas(TipoDestinatarioNotificacao tipoDestinatario, Long idDestinatario);

    void marcarComoLida(Long idNotificacao, TipoDestinatarioNotificacao tipoDestinatario,
                         Long idDestinatario);

    int marcarTodasComoLidas(TipoDestinatarioNotificacao tipoDestinatario, Long idDestinatario);

    int limparExpiradas();
}
