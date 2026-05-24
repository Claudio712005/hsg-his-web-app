package br.com.hsg.dao;

import br.com.hsg.domain.entity.RegraCobertura;
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
public class RegraCoberturaDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public RegraCobertura buscarPorId(Long id) {
        try {
            return em.createQuery(
                    "SELECT r FROM RegraCobertura r LEFT JOIN FETCH r.plano p LEFT JOIN FETCH p.convenio " +
                    "WHERE r.id = :id",
                    RegraCobertura.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<RegraCobertura> listarAtivasPorPlano(Long idPlano) {
        return em.createQuery(
                "SELECT r FROM RegraCobertura r WHERE r.status = :st AND r.plano.id = :idp " +
                "ORDER BY r.categoria ASC, r.procedimento ASC",
                RegraCobertura.class)
                .setParameter("st", IndicativoStatus.A)
                .setParameter("idp", idPlano)
                .getResultList();
    }

    public List<RegraCobertura> listarPaginado(int primeiro, int tamanho,
                                                Long filtroPlanoId, String filtroProcedimento,
                                                String filtroCategoria, String filtroStatus,
                                                String campoOrdenacao, boolean crescente) {
        StringBuilder sb = new StringBuilder(
                "SELECT r FROM RegraCobertura r LEFT JOIN FETCH r.plano p LEFT JOIN FETCH p.convenio c");
        Map<String, Object> params = new LinkedHashMap<>();
        List<String> condicoes = new ArrayList<>();

        if (filtroPlanoId != null) {
            condicoes.add("p.id = :idp");
            params.put("idp", filtroPlanoId);
        }
        if (filtroProcedimento != null && !filtroProcedimento.trim().isEmpty()) {
            condicoes.add("LOWER(r.procedimento) LIKE :fp");
            params.put("fp", "%" + filtroProcedimento.trim().toLowerCase() + "%");
        }
        if (filtroCategoria != null && !filtroCategoria.trim().isEmpty()) {
            condicoes.add("LOWER(r.categoria) LIKE :fc");
            params.put("fc", "%" + filtroCategoria.trim().toLowerCase() + "%");
        }
        if (filtroStatus != null && !filtroStatus.trim().isEmpty()) {
            condicoes.add("r.status = :st");
            params.put("st", IndicativoStatus.valueOf(filtroStatus));
        }
        if (!condicoes.isEmpty()) {
            sb.append(" WHERE ").append(String.join(" AND ", condicoes));
        }
        sb.append(resolverOrdenacao(campoOrdenacao, crescente));

        TypedQuery<RegraCobertura> q = em.createQuery(sb.toString(), RegraCobertura.class);
        params.forEach(q::setParameter);
        return q.setFirstResult(primeiro).setMaxResults(tamanho).getResultList();
    }

    public long contarTotal(Long filtroPlanoId, String filtroProcedimento,
                            String filtroCategoria, String filtroStatus) {
        StringBuilder sb = new StringBuilder(
                "SELECT COUNT(r) FROM RegraCobertura r LEFT JOIN r.plano p");
        Map<String, Object> params = new LinkedHashMap<>();
        List<String> condicoes = new ArrayList<>();

        if (filtroPlanoId != null) {
            condicoes.add("p.id = :idp");
            params.put("idp", filtroPlanoId);
        }
        if (filtroProcedimento != null && !filtroProcedimento.trim().isEmpty()) {
            condicoes.add("LOWER(r.procedimento) LIKE :fp");
            params.put("fp", "%" + filtroProcedimento.trim().toLowerCase() + "%");
        }
        if (filtroCategoria != null && !filtroCategoria.trim().isEmpty()) {
            condicoes.add("LOWER(r.categoria) LIKE :fc");
            params.put("fc", "%" + filtroCategoria.trim().toLowerCase() + "%");
        }
        if (filtroStatus != null && !filtroStatus.trim().isEmpty()) {
            condicoes.add("r.status = :st");
            params.put("st", IndicativoStatus.valueOf(filtroStatus));
        }
        if (!condicoes.isEmpty()) {
            sb.append(" WHERE ").append(String.join(" AND ", condicoes));
        }

        TypedQuery<Long> q = em.createQuery(sb.toString(), Long.class);
        params.forEach(q::setParameter);
        return q.getSingleResult();
    }

    public RegraCobertura buscarPorProcedimentoEPlano(Long idPlano, String procedimento) {
        if (procedimento == null || procedimento.trim().isEmpty()) return null;
        try {
            return em.createQuery(
                    "SELECT r FROM RegraCobertura r WHERE r.plano.id = :idp " +
                    "AND LOWER(r.procedimento) = LOWER(:proc)",
                    RegraCobertura.class)
                    .setParameter("idp", idPlano)
                    .setParameter("proc", procedimento.trim())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private String resolverOrdenacao(String campo, boolean crescente) {
        String dir = crescente ? "ASC" : "DESC";
        if ("procedimento".equals(campo)) return " ORDER BY r.procedimento " + dir;
        if ("categoria".equals(campo))    return " ORDER BY r.categoria " + dir + ", r.procedimento ASC";
        if ("plano".equals(campo))        return " ORDER BY c.nome " + dir + ", p.nome ASC, r.procedimento ASC";
        if ("plano.nome".equals(campo))   return " ORDER BY c.nome " + dir + ", p.nome ASC, r.procedimento ASC";
        if ("carenciaDias".equals(campo)) return " ORDER BY r.carenciaDias " + dir;
        if ("percentualCopagamento".equals(campo)) return " ORDER BY r.percentualCopagamento " + dir;
        if ("status".equals(campo))       return " ORDER BY r.status " + dir;
        if ("dataCadastro".equals(campo)) return " ORDER BY r.dataCadastro " + dir;
        return " ORDER BY r.categoria ASC, r.procedimento ASC";
    }

    public RegraCobertura salvar(RegraCobertura regra) {
        em.persist(regra);
        em.flush();
        return regra;
    }

    public RegraCobertura atualizar(RegraCobertura regra) {
        RegraCobertura merged = em.merge(regra);
        em.flush();
        return merged;
    }
}
