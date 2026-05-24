package br.com.hsg.dao;

import br.com.hsg.domain.entity.MedicoEspecialidade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class MedicoEspecialidadeDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public List<MedicoEspecialidade> listarPorMedico(Long idMedico) {
        return em.createQuery(
                "SELECT me FROM MedicoEspecialidade me " +
                "JOIN FETCH me.especialidade " +
                "WHERE me.medico.id = :idm " +
                "ORDER BY me.principal DESC, me.especialidade.nome ASC",
                MedicoEspecialidade.class)
                .setParameter("idm", idMedico)
                .getResultList();
    }

    public MedicoEspecialidade buscarPrincipal(Long idMedico) {
        try {
            return em.createQuery(
                    "SELECT me FROM MedicoEspecialidade me " +
                    "JOIN FETCH me.especialidade " +
                    "WHERE me.medico.id = :idm AND me.principal = 'S'",
                    MedicoEspecialidade.class)
                    .setParameter("idm", idMedico)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public MedicoEspecialidade buscarPorMedicoEspecialidade(Long idMedico, Long idEspecialidade) {
        try {
            return em.createQuery(
                    "SELECT me FROM MedicoEspecialidade me " +
                    "WHERE me.medico.id = :idm AND me.especialidade.id = :ide",
                    MedicoEspecialidade.class)
                    .setParameter("idm", idMedico)
                    .setParameter("ide", idEspecialidade)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public MedicoEspecialidade salvar(MedicoEspecialidade me) {
        em.persist(me);
        em.flush();
        return me;
    }

    public MedicoEspecialidade atualizar(MedicoEspecialidade me) {
        MedicoEspecialidade merged = em.merge(me);
        em.flush();
        return merged;
    }

    public void remover(Long id) {
        MedicoEspecialidade me = em.find(MedicoEspecialidade.class, id);
        if (me != null) em.remove(me);
    }
}
