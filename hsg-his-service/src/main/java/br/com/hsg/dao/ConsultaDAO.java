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

    public List<Consulta> listarProximasPorPaciente(Long idPaciente, LocalDateTime agora, int limite) {
        return em.createQuery(
                "SELECT c FROM Consulta c " +
                "LEFT JOIN FETCH c.medico m " +
                "LEFT JOIN FETCH m.especialidade " +
                "LEFT JOIN FETCH c.slot " +
                "WHERE c.paciente.id = :idp AND c.dataConsulta >= :agora " +
                "AND c.status IN :ativas " +
                "ORDER BY c.dataConsulta ASC",
                Consulta.class)
                .setParameter("idp", idPaciente)
                .setParameter("agora", agora)
                .setParameter("ativas", Arrays.asList(StatusConsulta.AGENDADA, StatusConsulta.CONFIRMADA))
                .setMaxResults(limite)
                .getResultList();
    }

    public List<Consulta> listarProximasPorMedico(Long idMedico, LocalDateTime agora, int limite) {
        return em.createQuery(
                "SELECT c FROM Consulta c " +
                "LEFT JOIN FETCH c.paciente " +
                "LEFT JOIN FETCH c.especialidade " +
                "LEFT JOIN FETCH c.slot " +
                "WHERE c.medico.id = :idm AND c.dataConsulta >= :agora " +
                "AND c.status IN :ativas " +
                "ORDER BY c.dataConsulta ASC",
                Consulta.class)
                .setParameter("idm", idMedico)
                .setParameter("agora", agora)
                .setParameter("ativas", Arrays.asList(StatusConsulta.AGENDADA, StatusConsulta.CONFIRMADA))
                .setMaxResults(limite)
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

    public List<Consulta> listarPendentesAteLimite(LocalDateTime limite) {
        return em.createQuery(
                "SELECT c FROM Consulta c " +
                "LEFT JOIN FETCH c.medico m " +
                "LEFT JOIN FETCH c.paciente p " +
                "LEFT JOIN FETCH c.slot " +
                "WHERE c.dataConsulta < :lim " +
                "AND c.status IN :pendentes " +
                "ORDER BY c.dataConsulta ASC",
                Consulta.class)
                .setParameter("lim", limite)
                .setParameter("pendentes",
                        Arrays.asList(StatusConsulta.AGENDADA, StatusConsulta.CONFIRMADA))
                .getResultList();
    }

    public List<Consulta> listarDoDia(LocalDateTime inicioDia, LocalDateTime fimDia, Long idMedicoOpcional) {
        return listarPorPeriodo(inicioDia, fimDia, idMedicoOpcional, null, null, null);
    }

    public List<Consulta> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim,
                                            Long idMedicoOpcional, StatusConsulta statusFiltro,
                                            String termoPaciente, Long idEspecialidadeOpcional) {
        StringBuilder jpql = new StringBuilder(
                "SELECT c FROM Consulta c " +
                "LEFT JOIN FETCH c.paciente p " +
                "LEFT JOIN FETCH c.medico m " +
                "LEFT JOIN FETCH c.especialidade " +
                "LEFT JOIN FETCH c.slot " +
                "WHERE c.dataConsulta >= :ini AND c.dataConsulta < :fim");
        if (idMedicoOpcional != null) {
            jpql.append(" AND c.medico.id = :idm");
        }
        if (idEspecialidadeOpcional != null) {
            jpql.append(" AND c.especialidade.id = :ide");
        }
        if (statusFiltro != null) {
            jpql.append(" AND c.status = :st");
        }
        boolean temTermo = termoPaciente != null && !termoPaciente.trim().isEmpty();
        if (temTermo) {
            jpql.append(" AND LOWER(p.nome.primeiroNome) LIKE :termo");
        }
        jpql.append(" ORDER BY c.dataConsulta ASC");

        javax.persistence.TypedQuery<Consulta> q = em.createQuery(jpql.toString(), Consulta.class)
                .setParameter("ini", inicio)
                .setParameter("fim", fim);
        if (idMedicoOpcional != null)        q.setParameter("idm", idMedicoOpcional);
        if (idEspecialidadeOpcional != null) q.setParameter("ide", idEspecialidadeOpcional);
        if (statusFiltro != null)            q.setParameter("st",  statusFiltro);
        if (temTermo)                        q.setParameter("termo", "%" + termoPaciente.trim().toLowerCase() + "%");
        return q.getResultList();
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
