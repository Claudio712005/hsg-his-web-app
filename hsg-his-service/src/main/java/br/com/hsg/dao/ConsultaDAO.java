package br.com.hsg.dao;

import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.enums.StatusConsulta;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Stateless
public class ConsultaDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public Consulta buscarPorId(Long id) {
        return em.find(Consulta.class, id);
    }

    public Consulta buscarPorIdComMedico(Long id) {
        List<Consulta> r = em.createQuery(
                "SELECT c FROM Consulta c " +
                "LEFT JOIN FETCH c.medico m " +
                "LEFT JOIN FETCH m.especialidade " +
                "LEFT JOIN FETCH c.slot " +
                "WHERE c.id = :id",
                Consulta.class)
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList();
        return r.isEmpty() ? null : r.get(0);
    }

    public List<Consulta> listarPorPaciente(Long idPaciente) {
        return em.createQuery(
                "SELECT c FROM Consulta c " +
                "LEFT JOIN FETCH c.medico m " +
                "LEFT JOIN FETCH m.especialidade " +
                "LEFT JOIN FETCH c.slot " +
                "WHERE c.paciente.id = :idp " +
                "ORDER BY c.dataConsulta DESC",
                Consulta.class)
                .setParameter("idp", idPaciente)
                .getResultList();
    }

    public long contarFuturasAtivasPorPaciente(Long idPaciente, LocalDateTime agora) {
        return em.createQuery(
                "SELECT COUNT(c) FROM Consulta c " +
                "WHERE c.paciente.id = :idp AND c.dataConsulta >= :agora " +
                "AND c.status IN :ativas",
                Long.class)
                .setParameter("idp", idPaciente)
                .setParameter("agora", agora)
                .setParameter("ativas", Arrays.asList(StatusConsulta.AGENDADA, StatusConsulta.CONFIRMADA))
                .getSingleResult();
    }

    public Consulta salvar(Consulta c) {
        em.persist(c);
        em.flush();
        return c;
    }

    public Consulta atualizar(Consulta c) {
        return em.merge(c);
    }
}
