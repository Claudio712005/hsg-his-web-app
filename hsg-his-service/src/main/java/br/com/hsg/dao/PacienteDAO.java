package br.com.hsg.dao;

import br.com.hsg.domain.entity.Paciente;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

@Stateless
public class PacienteDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public Paciente buscarPorKeycloakId(String keycloakId) {
        try {
            return em.createQuery(
                    "SELECT p FROM Paciente p " +
                    "JOIN FETCH p.contaUsuario c " +
                    "WHERE c.keycloakId = :kcId",
                    Paciente.class
            ).setParameter("kcId", keycloakId)
             .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Paciente buscarPorId(Long id) {
        try {
            return em.createQuery(
                    "SELECT p FROM Paciente p WHERE p.id = :id",
                    Paciente.class
            ).setParameter("id", id)
             .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Paciente> buscarPorTermo(String termo, int limite) {
        if (termo == null || termo.trim().length() < 2) return Collections.emptyList();
        String like = "%" + termo.trim().toLowerCase() + "%";
        return em.createQuery(
                "SELECT p FROM Paciente p " +
                "WHERE LOWER(p.nome.primeiroNome) LIKE :t " +
                "   OR LOWER(p.nome.sobrenome) LIKE :t " +
                "   OR LOWER(CONCAT(p.nome.primeiroNome,' ',p.nome.sobrenome)) LIKE :t " +
                "ORDER BY p.nome.primeiroNome ASC, p.nome.sobrenome ASC",
                Paciente.class)
                .setParameter("t", like)
                .setMaxResults(limite > 0 ? limite : 10)
                .getResultList();
    }
}
