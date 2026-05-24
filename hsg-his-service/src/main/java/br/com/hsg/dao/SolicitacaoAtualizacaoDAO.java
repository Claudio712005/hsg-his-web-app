package br.com.hsg.dao;

import br.com.hsg.domain.entity.SolicitacaoAtualizacao;
import br.com.hsg.domain.enums.TipoSolicitacao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class SolicitacaoAtualizacaoDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public SolicitacaoAtualizacao salvar(SolicitacaoAtualizacao solicitacao) {
        em.persist(solicitacao);
        em.flush();
        return solicitacao;
    }

    public SolicitacaoAtualizacao atualizar(SolicitacaoAtualizacao solicitacao) {
        SolicitacaoAtualizacao merged = em.merge(solicitacao);
        em.flush();
        return merged;
    }

    public SolicitacaoAtualizacao buscarPorId(Long id) {
        try {
            return em.createQuery(
                    "SELECT s FROM SolicitacaoAtualizacao s WHERE s.id = :id",
                    SolicitacaoAtualizacao.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            return null;
        }
    }

    public List<SolicitacaoAtualizacao> buscarPorPaciente(
            Long pacienteId, int inicio, int tamanho, List<TipoSolicitacao> tipos) {

        String jpql = "SELECT s FROM SolicitacaoAtualizacao s " +
                      "LEFT JOIN FETCH s.enfermeiro " +
                      "WHERE s.paciente.id = :pacienteId";
        if (tipos != null && !tipos.isEmpty()) {
            jpql += " AND s.tipoSolicitacao IN :tipos";
        }
        jpql += " ORDER BY s.dataCadastro DESC";

        TypedQuery<SolicitacaoAtualizacao> q = em.createQuery(jpql, SolicitacaoAtualizacao.class)
                .setParameter("pacienteId", pacienteId)
                .setFirstResult(inicio)
                .setMaxResults(tamanho);

        if (tipos != null && !tipos.isEmpty()) {
            q.setParameter("tipos", tipos);
        }
        return q.getResultList();
    }

    public long contarPorPaciente(Long pacienteId, List<TipoSolicitacao> tipos) {
        String jpql = "SELECT COUNT(s) FROM SolicitacaoAtualizacao s " +
                      "WHERE s.paciente.id = :pacienteId";
        if (tipos != null && !tipos.isEmpty()) {
            jpql += " AND s.tipoSolicitacao IN :tipos";
        }

        TypedQuery<Long> q = em.createQuery(jpql, Long.class)
                .setParameter("pacienteId", pacienteId);

        if (tipos != null && !tipos.isEmpty()) {
            q.setParameter("tipos", tipos);
        }
        return q.getSingleResult();
    }
}
