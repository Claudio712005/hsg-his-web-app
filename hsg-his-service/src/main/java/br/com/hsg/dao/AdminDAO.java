package br.com.hsg.dao;

import br.com.hsg.domain.entity.Admin;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class AdminDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public Admin buscarPorId(Long id) {
        return em.find(Admin.class, id);
    }

    public Admin buscarPorKeycloakId(String keycloakId) {
        List<Admin> result = em.createQuery(
                "SELECT a FROM Admin a JOIN FETCH a.contaUsuario c WHERE c.keycloakId = :kcId",
                Admin.class)
                .setParameter("kcId", keycloakId)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public long contarTotal() {
        return em.createQuery("SELECT COUNT(a) FROM Admin a", Long.class).getSingleResult();
    }
}
