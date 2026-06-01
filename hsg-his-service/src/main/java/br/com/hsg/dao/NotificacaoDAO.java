package br.com.hsg.dao;

import br.com.hsg.domain.entity.Notificacao;
import br.com.hsg.domain.enums.TipoDestinatarioNotificacao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class NotificacaoDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public Notificacao buscarPorId(Long id) {
        return em.find(Notificacao.class, id);
    }

    public List<Notificacao> listarPorDestinatario(TipoDestinatarioNotificacao tipo, Long idDestinatario,
                                                    int primeiro, int tamanho) {
        return listarFiltrado(tipo, idDestinatario, null, null, primeiro, tamanho);
    }

    public List<Notificacao> listarFiltrado(TipoDestinatarioNotificacao tipo, Long idDestinatario,
                                             Boolean lidaFiltro, String termoBusca,
                                             int primeiro, int tamanho) {
        StringBuilder jpql = new StringBuilder(
                "SELECT n FROM Notificacao n " +
                "WHERE n.tipoDestinatario = :td AND n.idDestinatario = :ide");
        if (lidaFiltro != null) {
            jpql.append(" AND n.lida = :lida");
        }
        boolean temBusca = termoBusca != null && !termoBusca.trim().isEmpty();
        if (temBusca) {
            jpql.append(" AND (LOWER(n.titulo) LIKE :termo OR LOWER(n.mensagem) LIKE :termo)");
        }
        jpql.append(" ORDER BY n.dataCriacao DESC");

        javax.persistence.TypedQuery<Notificacao> q = em.createQuery(jpql.toString(), Notificacao.class)
                .setParameter("td", tipo)
                .setParameter("ide", idDestinatario);
        if (lidaFiltro != null) {
            q.setParameter("lida", lidaFiltro ? "S" : "N");
        }
        if (temBusca) {
            q.setParameter("termo", "%" + termoBusca.trim().toLowerCase() + "%");
        }
        return q.setFirstResult(primeiro).setMaxResults(tamanho).getResultList();
    }

    public long contarNaoLidas(TipoDestinatarioNotificacao tipo, Long idDestinatario) {
        return em.createQuery(
                "SELECT COUNT(n) FROM Notificacao n " +
                "WHERE n.tipoDestinatario = :td AND n.idDestinatario = :ide AND n.lida = 'N'",
                Long.class)
                .setParameter("td", tipo)
                .setParameter("ide", idDestinatario)
                .getSingleResult();
    }

    public int marcarTodasComoLidas(TipoDestinatarioNotificacao tipo, Long idDestinatario,
                                     LocalDateTime agora) {
        return em.createQuery(
                "UPDATE Notificacao n SET n.lida = 'S', n.dataLeitura = :agora " +
                "WHERE n.tipoDestinatario = :td AND n.idDestinatario = :ide AND n.lida = 'N'")
                .setParameter("td", tipo)
                .setParameter("ide", idDestinatario)
                .setParameter("agora", agora)
                .executeUpdate();
    }

    public int removerExpiradas(LocalDateTime limite) {
        return em.createQuery(
                "DELETE FROM Notificacao n WHERE n.dataExpiracao < :lim")
                .setParameter("lim", limite)
                .executeUpdate();
    }

    public Notificacao salvar(Notificacao n) {
        em.persist(n);
        return n;
    }

    public Notificacao atualizar(Notificacao n) {
        return em.merge(n);
    }
}
