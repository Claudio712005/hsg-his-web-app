package br.com.hsg.dao;

import br.com.hsg.domain.entity.Arquivo;
import br.com.hsg.domain.enums.IndicativoStatus;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ArquivoDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public Arquivo salvar(Arquivo a) {
        em.persist(a);
        return a;
    }

    public Arquivo atualizar(Arquivo a) {
        return em.merge(a);
    }

    public Arquivo buscarPorId(Long id) {
        if (id == null) return null;
        return em.find(Arquivo.class, id);
    }

    public Arquivo buscarPorPathLogico(String pathLogico) {
        if (pathLogico == null) return null;
        try {
            return em.createQuery(
                    "SELECT a FROM Arquivo a WHERE a.pathLogico = :p", Arquivo.class)
                    .setParameter("p", pathLogico)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<Arquivo> listarPorConsulta(Long idConsulta) {
        return em.createQuery(
                "SELECT a FROM Arquivo a " +
                "WHERE a.idConsulta = :idc AND a.status = :st " +
                "ORDER BY a.dataUpload DESC",
                Arquivo.class)
                .setParameter("idc", idConsulta)
                .setParameter("st", IndicativoStatus.A)
                .getResultList();
    }

    public List<Arquivo> listarPorAnotacao(Long idAnotacao) {
        return em.createQuery(
                "SELECT a FROM Arquivo a " +
                "WHERE a.idAnotacao = :ida AND a.status = :st " +
                "ORDER BY a.dataUpload DESC",
                Arquivo.class)
                .setParameter("ida", idAnotacao)
                .setParameter("st", IndicativoStatus.A)
                .getResultList();
    }

    public List<Arquivo> listarPorPaciente(Long idPaciente) {
        return em.createQuery(
                "SELECT a FROM Arquivo a " +
                "WHERE a.idPaciente = :idp AND a.status = :st " +
                "ORDER BY a.dataUpload DESC",
                Arquivo.class)
                .setParameter("idp", idPaciente)
                .setParameter("st", IndicativoStatus.A)
                .getResultList();
    }

    public List<Arquivo> listarInativosParaGc(int limite) {
        return em.createQuery(
                "SELECT a FROM Arquivo a WHERE a.status = :st ORDER BY a.dataUpload ASC",
                Arquivo.class)
                .setParameter("st", IndicativoStatus.I)
                .setMaxResults(limite)
                .getResultList();
    }
}
