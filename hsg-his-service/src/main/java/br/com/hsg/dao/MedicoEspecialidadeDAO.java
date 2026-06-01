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

    public List<br.com.hsg.domain.entity.Medico> listarMedicosPorEspecialidade(Long idEspecialidade) {
        return em.createQuery(
                "SELECT DISTINCT m FROM MedicoEspecialidade me " +
                "JOIN me.medico m " +
                "WHERE me.especialidade.id = :ide AND m.status = :st " +
                "ORDER BY m.nome.primeiroNome ASC",
                br.com.hsg.domain.entity.Medico.class)
                .setParameter("ide", idEspecialidade)
                .setParameter("st", br.com.hsg.domain.enums.IndicativoStatus.A)
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
