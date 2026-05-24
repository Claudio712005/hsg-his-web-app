package br.com.hsg.dao;

import br.com.hsg.domain.entity.PreCadastroProfissional;
import br.com.hsg.domain.enums.StatusPreCadastro;
import br.com.hsg.domain.enums.TipoProfissional;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class PreCadastroProfissionalDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public PreCadastroProfissional salvar(PreCadastroProfissional preCadastro) {
        em.persist(preCadastro);
        em.flush();
        return preCadastro;
    }

    public PreCadastroProfissional atualizar(PreCadastroProfissional preCadastro) {
        PreCadastroProfissional merged = em.merge(preCadastro);
        em.flush();
        return merged;
    }

    public PreCadastroProfissional buscarPorId(Long id) {
        try {
            return em.createQuery(
                    "SELECT p FROM PreCadastroProfissional p WHERE p.id = :id",
                    PreCadastroProfissional.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public PreCadastroProfissional buscarPorToken(String token) {
        try {
            return em.createQuery(
                    "SELECT p FROM PreCadastroProfissional p WHERE p.tokenConvite = :token",
                    PreCadastroProfissional.class)
                    .setParameter("token", token)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public PreCadastroProfissional buscarPorEmail(String email) {
        try {
            return em.createQuery(
                    "SELECT p FROM PreCadastroProfissional p WHERE p.emailPessoal = :email",
                    PreCadastroProfissional.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public PreCadastroProfissional buscarPorCpf(String cpf) {
        try {
            return em.createQuery(
                    "SELECT p FROM PreCadastroProfissional p WHERE p.cpf = :cpf",
                    PreCadastroProfissional.class)
                    .setParameter("cpf", cpf)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<PreCadastroProfissional> listarTodos(int inicio, int tamanho) {
        return em.createQuery(
                "SELECT p FROM PreCadastroProfissional p ORDER BY p.dataCriacao DESC",
                PreCadastroProfissional.class)
                .setFirstResult(inicio)
                .setMaxResults(tamanho)
                .getResultList();
    }

    public List<PreCadastroProfissional> listarPorStatus(StatusPreCadastro status, int inicio, int tamanho) {
        return em.createQuery(
                "SELECT p FROM PreCadastroProfissional p WHERE p.status = :status ORDER BY p.dataCriacao DESC",
                PreCadastroProfissional.class)
                .setParameter("status", status)
                .setFirstResult(inicio)
                .setMaxResults(tamanho)
                .getResultList();
    }

    public List<PreCadastroProfissional> listarPorTipo(TipoProfissional tipo, int inicio, int tamanho) {
        return em.createQuery(
                "SELECT p FROM PreCadastroProfissional p WHERE p.tipoProfissional = :tipo ORDER BY p.dataCriacao DESC",
                PreCadastroProfissional.class)
                .setParameter("tipo", tipo)
                .setFirstResult(inicio)
                .setMaxResults(tamanho)
                .getResultList();
    }

    public long contar() {
        return em.createQuery("SELECT COUNT(p) FROM PreCadastroProfissional p", Long.class)
                .getSingleResult();
    }

    public long contarPorStatus(StatusPreCadastro status) {
        return em.createQuery(
                "SELECT COUNT(p) FROM PreCadastroProfissional p WHERE p.status = :status", Long.class)
                .setParameter("status", status)
                .getSingleResult();
    }

    public boolean existeEmailPendente(String email) {
        Long count = em.createQuery(
                "SELECT COUNT(p) FROM PreCadastroProfissional p WHERE p.emailPessoal = :email AND p.status = :status",
                Long.class)
                .setParameter("email", email)
                .setParameter("status", StatusPreCadastro.PENDENTE)
                .getSingleResult();
        return count > 0;
    }

    public boolean existeEmailCorporativo(String emailCorp) {
        Long count = em.createQuery(
                "SELECT COUNT(p) FROM PreCadastroProfissional p WHERE p.emailCorporativo = :emailCorp",
                Long.class)
                .setParameter("emailCorp", emailCorp)
                .getSingleResult();
        return count > 0;
    }

    public boolean existeCpfPendente(String cpf) {
        Long count = em.createQuery(
                "SELECT COUNT(p) FROM PreCadastroProfissional p WHERE p.cpf = :cpf AND p.status = :status",
                Long.class)
                .setParameter("cpf", cpf)
                .setParameter("status", StatusPreCadastro.PENDENTE)
                .getSingleResult();
        return count > 0;
    }
}
