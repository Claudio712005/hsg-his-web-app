package br.com.hsg.dao;

import br.com.hsg.domain.entity.Receita;
import br.com.hsg.domain.enums.IndicativoStatus;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Stateless
public class ReceitaDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public Receita salvar(Receita r) {
        em.persist(r);
        return r;
    }

    public Receita atualizar(Receita r) {
        return em.merge(r);
    }

    public Receita buscarAtivaPorConsulta(Long idConsulta) {
        if (idConsulta == null) return null;
        try {
            return em.createQuery(
                    "SELECT r FROM Receita r " +
                    "LEFT JOIN FETCH r.itens " +
                    "LEFT JOIN FETCH r.medico m " +
                    "LEFT JOIN FETCH m.especialidade " +
                    "LEFT JOIN FETCH r.consulta c " +
                    "LEFT JOIN FETCH c.paciente " +
                    "WHERE r.consulta.id = :idc AND r.status = :st",
                    Receita.class)
                    .setParameter("idc", idConsulta)
                    .setParameter("st", IndicativoStatus.A)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public java.util.List<Receita> listarTodasPorConsulta(Long idConsulta) {
        if (idConsulta == null) return java.util.Collections.emptyList();
        return em.createQuery(
                "SELECT DISTINCT r FROM Receita r " +
                "LEFT JOIN FETCH r.itens " +
                "WHERE r.consulta.id = :idc " +
                "ORDER BY r.dataEmissao DESC",
                Receita.class)
                .setParameter("idc", idConsulta)
                .getResultList();
    }

    public Receita buscarPorId(Long id) {
        if (id == null) return null;
        return em.find(Receita.class, id);
    }

    public int inativarAtivasPorConsulta(Long idConsulta) {
        if (idConsulta == null) return 0;
        int n = em.createQuery(
                "UPDATE Receita r SET r.status = :sti " +
                "WHERE r.consulta.id = :idc AND r.status = :sta")
                .setParameter("sti", IndicativoStatus.I)
                .setParameter("sta", IndicativoStatus.A)
                .setParameter("idc", idConsulta)
                .executeUpdate();
        em.flush();
        return n;
    }
}
