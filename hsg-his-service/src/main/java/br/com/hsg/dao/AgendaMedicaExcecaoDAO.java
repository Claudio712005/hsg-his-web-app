package br.com.hsg.dao;

import br.com.hsg.domain.entity.AgendaMedicaExcecao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class AgendaMedicaExcecaoDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public AgendaMedicaExcecao buscarPorId(Long id) {
        return em.find(AgendaMedicaExcecao.class, id);
    }

    public List<AgendaMedicaExcecao> listarPorMedico(Long idMedico) {
        return em.createQuery(
                "SELECT e FROM AgendaMedicaExcecao e WHERE e.medico.id = :idm " +
                "ORDER BY e.dataInicio DESC",
                AgendaMedicaExcecao.class)
                .setParameter("idm", idMedico)
                .getResultList();
    }

    public List<AgendaMedicaExcecao> listarVigentesNoPeriodo(Long idMedico,
                                                              LocalDateTime inicio,
                                                              LocalDateTime fim) {
        return em.createQuery(
                "SELECT e FROM AgendaMedicaExcecao e " +
                "WHERE e.medico.id = :idm AND e.dataInicio < :fim AND e.dataFim > :ini " +
                "ORDER BY e.dataInicio ASC",
                AgendaMedicaExcecao.class)
                .setParameter("idm", idMedico)
                .setParameter("ini", inicio)
                .setParameter("fim", fim)
                .getResultList();
    }

    public AgendaMedicaExcecao salvar(AgendaMedicaExcecao e) {
        em.persist(e);
        em.flush();
        return e;
    }

    public void remover(Long id) {
        AgendaMedicaExcecao e = em.find(AgendaMedicaExcecao.class, id);
        if (e != null) em.remove(e);
    }
}
