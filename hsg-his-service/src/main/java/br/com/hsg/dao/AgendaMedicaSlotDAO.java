package br.com.hsg.dao;

import br.com.hsg.domain.entity.AgendaMedicaSlot;
import br.com.hsg.domain.enums.StatusSlotAgenda;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class AgendaMedicaSlotDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public AgendaMedicaSlot buscarPorId(Long id) {
        return em.find(AgendaMedicaSlot.class, id);
    }

    public List<AgendaMedicaSlot> listarPorMedicoPeriodo(Long idMedico,
                                                          LocalDateTime inicio,
                                                          LocalDateTime fim) {
        return em.createQuery(
                "SELECT s FROM AgendaMedicaSlot s " +
                "WHERE s.medico.id = :idm AND s.dataInicio >= :ini AND s.dataInicio < :fim " +
                "ORDER BY s.dataInicio ASC",
                AgendaMedicaSlot.class)
                .setParameter("idm", idMedico)
                .setParameter("ini", inicio)
                .setParameter("fim", fim)
                .getResultList();
    }

    public List<AgendaMedicaSlot> listarLivresPorMedicoPeriodo(Long idMedico,
                                                                LocalDateTime inicio,
                                                                LocalDateTime fim) {
        return em.createQuery(
                "SELECT s FROM AgendaMedicaSlot s " +
                "WHERE s.medico.id = :idm AND s.status = :st " +
                "AND s.dataInicio >= :ini AND s.dataInicio < :fim " +
                "ORDER BY s.dataInicio ASC",
                AgendaMedicaSlot.class)
                .setParameter("idm", idMedico)
                .setParameter("st",  StatusSlotAgenda.LIVRE)
                .setParameter("ini", inicio)
                .setParameter("fim", fim)
                .getResultList();
    }

    public boolean existePorMedicoDataInicio(Long idMedico, LocalDateTime dataInicio) {
        try {
            em.createQuery(
                    "SELECT s.id FROM AgendaMedicaSlot s " +
                    "WHERE s.medico.id = :idm AND s.dataInicio = :dti",
                    Long.class)
                    .setParameter("idm", idMedico)
                    .setParameter("dti", dataInicio)
                    .setMaxResults(1)
                    .getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    public long contarPorMedicoStatusPeriodo(Long idMedico, StatusSlotAgenda status,
                                              LocalDateTime inicio, LocalDateTime fim) {
        return em.createQuery(
                "SELECT COUNT(s) FROM AgendaMedicaSlot s " +
                "WHERE s.medico.id = :idm AND s.status = :st " +
                "AND s.dataInicio >= :ini AND s.dataInicio < :fim",
                Long.class)
                .setParameter("idm", idMedico)
                .setParameter("st",  status)
                .setParameter("ini", inicio)
                .setParameter("fim", fim)
                .getSingleResult();
    }

    public AgendaMedicaSlot salvar(AgendaMedicaSlot s) {
        em.persist(s);
        return s;
    }

    public AgendaMedicaSlot atualizar(AgendaMedicaSlot s) {
        return em.merge(s);
    }

    public void flush() {
        em.flush();
    }
}
