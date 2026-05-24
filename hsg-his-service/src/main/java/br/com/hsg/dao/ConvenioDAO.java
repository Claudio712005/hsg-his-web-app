package br.com.hsg.dao;

import br.com.hsg.domain.entity.Convenio;
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
public class ConvenioDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public Convenio buscarPorId(Long id) {
        return em.find(Convenio.class, id);
    }

    public Convenio buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) return null;
        try {
            return em.createQuery(
                    "SELECT c FROM Convenio c WHERE LOWER(c.nome) = LOWER(:nome)",
                    Convenio.class)
                    .setParameter("nome", nome.trim())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Convenio> listarAtivos() {
        return em.createQuery(
                "SELECT c FROM Convenio c WHERE c.status = :st ORDER BY c.nome ASC",
                Convenio.class)
                .setParameter("st", IndicativoStatus.A)
                .getResultList();
    }

    public List<Convenio> listarPaginado(int primeiro, int tamanho,
                                          String filtroNome, String filtroStatus,
                                          String campoOrdenacao, boolean crescente) {
        StringBuilder sb = new StringBuilder("SELECT c FROM Convenio c");
        Map<String, Object> params = new LinkedHashMap<>();
        List<String> condicoes = new ArrayList<>();

        if (filtroNome != null && !filtroNome.trim().isEmpty()) {
            condicoes.add("LOWER(c.nome) LIKE :fn");
            params.put("fn", "%" + filtroNome.trim().toLowerCase() + "%");
        }
        if (filtroStatus != null && !filtroStatus.trim().isEmpty()) {
            condicoes.add("c.status = :st");
            params.put("st", IndicativoStatus.valueOf(filtroStatus));
        }
        if (!condicoes.isEmpty()) {
            sb.append(" WHERE ").append(String.join(" AND ", condicoes));
        }
        sb.append(resolverOrdenacao(campoOrdenacao, crescente));

        TypedQuery<Convenio> q = em.createQuery(sb.toString(), Convenio.class);
        params.forEach(q::setParameter);
        return q.setFirstResult(primeiro).setMaxResults(tamanho).getResultList();
    }

    public long contarTotal(String filtroNome, String filtroStatus) {
        StringBuilder sb = new StringBuilder("SELECT COUNT(c) FROM Convenio c");
        Map<String, Object> params = new LinkedHashMap<>();
        List<String> condicoes = new ArrayList<>();

        if (filtroNome != null && !filtroNome.trim().isEmpty()) {
            condicoes.add("LOWER(c.nome) LIKE :fn");
            params.put("fn", "%" + filtroNome.trim().toLowerCase() + "%");
        }
        if (filtroStatus != null && !filtroStatus.trim().isEmpty()) {
            condicoes.add("c.status = :st");
            params.put("st", IndicativoStatus.valueOf(filtroStatus));
        }
        if (!condicoes.isEmpty()) {
            sb.append(" WHERE ").append(String.join(" AND ", condicoes));
        }

        TypedQuery<Long> q = em.createQuery(sb.toString(), Long.class);
        params.forEach(q::setParameter);
        return q.getSingleResult();
    }

    public long contarAtivos() {
        return em.createQuery(
                "SELECT COUNT(c) FROM Convenio c WHERE c.status = :st",
                Long.class)
                .setParameter("st", IndicativoStatus.A)
                .getSingleResult();
    }

    private String resolverOrdenacao(String campo, boolean crescente) {
        String dir = crescente ? "ASC" : "DESC";
        if ("nome".equals(campo))         return " ORDER BY c.nome " + dir;
        if ("registroAns".equals(campo))  return " ORDER BY c.registroAns " + dir;
        if ("status".equals(campo))       return " ORDER BY c.status " + dir;
        if ("dataCadastro".equals(campo)) return " ORDER BY c.dataCadastro " + dir;
        return " ORDER BY c.nome ASC";
    }

    public Convenio salvar(Convenio convenio) {
        em.persist(convenio);
        em.flush();
        return convenio;
    }

    public Convenio atualizar(Convenio convenio) {
        Convenio merged = em.merge(convenio);
        em.flush();
        return merged;
    }
}
