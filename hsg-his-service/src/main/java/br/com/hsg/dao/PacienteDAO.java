package br.com.hsg.dao;

import br.com.hsg.domain.entity.Paciente;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

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
}
