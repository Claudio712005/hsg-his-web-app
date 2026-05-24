package br.com.hsg.dao;

import br.com.hsg.domain.entity.Enfermeiro;
import br.com.hsg.domain.enums.IndicativoStatus;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class EnfermeiroDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public List<Enfermeiro> listarPaginado(int primeiro, int tamanho,
                                           String filtroNome, String filtroSetor,
                                           String filtroStatus, String campoOrdenacao,
                                           boolean crescente) {
        StringBuilder sb = new StringBuilder(
            "SELECT enf FROM Enfermeiro enf JOIN FETCH enf.contaUsuario"
        );
        Map<String, Object> params = new LinkedHashMap<>();
        List<String> condicoes = new ArrayList<>();

        if (filtroNome != null && !filtroNome.trim().isEmpty()) {
            condicoes.add("(LOWER(enf.nome.primeiroNome) LIKE :fn OR LOWER(enf.nome.sobrenome) LIKE :fn)");
            params.put("fn", "%" + filtroNome.trim().toLowerCase() + "%");
        }
        if (filtroSetor != null && !filtroSetor.trim().isEmpty()) {
            condicoes.add("LOWER(enf.setor) LIKE :st");
            params.put("st", "%" + filtroSetor.trim().toLowerCase() + "%");
        }
        if (filtroStatus != null && !filtroStatus.trim().isEmpty()) {
            condicoes.add("enf.status = :sts");
            params.put("sts", IndicativoStatus.valueOf(filtroStatus));
        }
        if (!condicoes.isEmpty()) {
            sb.append(" WHERE ").append(String.join(" AND ", condicoes));
        }
        sb.append(resolverOrdenacao(campoOrdenacao, crescente));

        TypedQuery<Enfermeiro> q = em.createQuery(sb.toString(), Enfermeiro.class);
        params.forEach(q::setParameter);
        return q.setFirstResult(primeiro).setMaxResults(tamanho).getResultList();
    }

    public long contarTotal(String filtroNome, String filtroSetor, String filtroStatus) {
        StringBuilder sb = new StringBuilder("SELECT COUNT(enf) FROM Enfermeiro enf");
        Map<String, Object> params = new LinkedHashMap<>();
        List<String> condicoes = new ArrayList<>();

        if (filtroNome != null && !filtroNome.trim().isEmpty()) {
            condicoes.add("(LOWER(enf.nome.primeiroNome) LIKE :fn OR LOWER(enf.nome.sobrenome) LIKE :fn)");
            params.put("fn", "%" + filtroNome.trim().toLowerCase() + "%");
        }
        if (filtroSetor != null && !filtroSetor.trim().isEmpty()) {
            condicoes.add("LOWER(enf.setor) LIKE :st");
            params.put("st", "%" + filtroSetor.trim().toLowerCase() + "%");
        }
        if (filtroStatus != null && !filtroStatus.trim().isEmpty()) {
            condicoes.add("enf.status = :sts");
            params.put("sts", IndicativoStatus.valueOf(filtroStatus));
        }
        if (!condicoes.isEmpty()) {
            sb.append(" WHERE ").append(String.join(" AND ", condicoes));
        }

        TypedQuery<Long> q = em.createQuery(sb.toString(), Long.class);
        params.forEach(q::setParameter);
        return q.getSingleResult();
    }

    private String resolverOrdenacao(String campo, boolean crescente) {
        String dir = crescente ? "ASC" : "DESC";
        if ("nome".equals(campo))          return " ORDER BY enf.nome.primeiroNome " + dir + ", enf.nome.sobrenome " + dir;
        if ("coren.numero".equals(campo))  return " ORDER BY enf.coren.numero " + dir;
        if ("setor".equals(campo))         return " ORDER BY enf.setor " + dir;
        if ("status".equals(campo))        return " ORDER BY enf.status " + dir;
        if ("dataCadastro".equals(campo))  return " ORDER BY enf.dataCadastro " + dir;
        return " ORDER BY enf.nome.primeiroNome ASC, enf.nome.sobrenome ASC";
    }

    public Enfermeiro buscarPorId(Long id) {
        try {
            return em.createQuery(
                    "SELECT e FROM Enfermeiro e JOIN FETCH e.contaUsuario WHERE e.id = :id",
                    Enfermeiro.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Enfermeiro salvar(Enfermeiro enfermeiro) {
        em.persist(enfermeiro);
        em.flush();
        return enfermeiro;
    }

    public Enfermeiro buscarPorKeycloakId(String keycloakId) {
        try {
            return em.createQuery(
                    "SELECT e FROM Enfermeiro e JOIN FETCH e.contaUsuario c WHERE c.keycloakId = :kcId",
                    Enfermeiro.class)
                    .setParameter("kcId", keycloakId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
