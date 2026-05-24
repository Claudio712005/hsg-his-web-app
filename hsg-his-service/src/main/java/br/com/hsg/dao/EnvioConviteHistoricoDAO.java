package br.com.hsg.dao;

import br.com.hsg.domain.entity.EnvioConviteHistorico;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class EnvioConviteHistoricoDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public EnvioConviteHistorico salvar(EnvioConviteHistorico historico) {
        em.persist(historico);
        em.flush();
        return historico;
    }

    public EnvioConviteHistorico atualizar(EnvioConviteHistorico historico) {
        EnvioConviteHistorico merged = em.merge(historico);
        em.flush();
        return merged;
    }

    public List<EnvioConviteHistorico> listarPorPreCadastro(Long preCadastroId) {
        return em.createQuery(
                "SELECT h FROM EnvioConviteHistorico h " +
                "WHERE h.preCadastro.id = :id " +
                "ORDER BY h.dataEnvio DESC",
                EnvioConviteHistorico.class)
                .setParameter("id", preCadastroId)
                .getResultList();
    }

    public long contarPorPreCadastro(Long preCadastroId) {
        return em.createQuery(
                "SELECT COUNT(h) FROM EnvioConviteHistorico h WHERE h.preCadastro.id = :id",
                Long.class)
                .setParameter("id", preCadastroId)
                .getSingleResult();
    }
}
