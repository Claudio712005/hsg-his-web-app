package br.com.hsg.dao;

import br.com.hsg.domain.entity.AgendaMedica;
import br.com.hsg.domain.enums.DiaSemana;
import br.com.hsg.domain.enums.IndicativoStatus;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalTime;
import java.util.List;

@Stateless
public class AgendaMedicaDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public AgendaMedica buscarPorId(Long id) {
        return em.find(AgendaMedica.class, id);
    }

    public List<AgendaMedica> listarPorMedico(Long idMedico) {
        return em.createQuery(
                "SELECT a FROM AgendaMedica a WHERE a.medico.id = :idm " +
                "ORDER BY a.diaSemana ASC, a.horaInicio ASC",
                AgendaMedica.class)
                .setParameter("idm", idMedico)
                .getResultList();
    }

    public List<AgendaMedica> listarAtivasPorMedico(Long idMedico) {
        return em.createQuery(
                "SELECT a FROM AgendaMedica a WHERE a.medico.id = :idm AND a.status = :st " +
                "ORDER BY a.diaSemana ASC, a.horaInicio ASC",
                AgendaMedica.class)
                .setParameter("idm", idMedico)
                .setParameter("st", IndicativoStatus.A)
                .getResultList();
    }

    public List<AgendaMedica> buscarSobreposicao(Long idMedico, DiaSemana diaSemana,
                                                  LocalTime horaInicio, LocalTime horaFim, Long idExcluir) {
        String jpql = "SELECT a FROM AgendaMedica a " +
                "WHERE a.medico.id = :idm AND a.diaSemana = :dow AND a.status = :st " +
                "AND a.horaInicio < :fim AND a.horaFim > :ini" +
                (idExcluir != null ? " AND a.id <> :idEx" : "");
        javax.persistence.TypedQuery<AgendaMedica> q = em.createQuery(jpql, AgendaMedica.class)
                .setParameter("idm", idMedico)
                .setParameter("dow", diaSemana)
                .setParameter("st",  IndicativoStatus.A)
                .setParameter("ini", horaInicio)
                .setParameter("fim", horaFim);
        if (idExcluir != null) q.setParameter("idEx", idExcluir);
        return q.getResultList();
    }

    public AgendaMedica salvar(AgendaMedica a) {
        em.persist(a);
        em.flush();
        return a;
    }

    public AgendaMedica atualizar(AgendaMedica a) {
        AgendaMedica merged = em.merge(a);
        em.flush();
        return merged;
    }
}
