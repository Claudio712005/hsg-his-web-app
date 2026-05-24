package br.com.hsg.dao;

import br.com.hsg.domain.entity.PlanoConvenio;
import br.com.hsg.domain.enums.IndicativoStatus;
import br.com.hsg.domain.enums.TipoCoberturaPlano;

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
public class PlanoConvenioDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public PlanoConvenio buscarPorId(Long id) {
        try {
            return em.createQuery(
                    "SELECT p FROM PlanoConvenio p LEFT JOIN FETCH p.convenio WHERE p.id = :id",
                    PlanoConvenio.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<PlanoConvenio> listarAtivosPorConvenio(Long idConvenio) {
        return em.createQuery(
                "SELECT p FROM PlanoConvenio p LEFT JOIN FETCH p.convenio c " +
                "WHERE p.status = :st AND c.id = :idc ORDER BY p.nome ASC",
                PlanoConvenio.class)
                .setParameter("st", IndicativoStatus.A)
                .setParameter("idc", idConvenio)
                .getResultList();
    }

    public List<PlanoConvenio> listarPaginado(int primeiro, int tamanho,
                                               Long filtroConvenioId, String filtroNome,
                                               String filtroCobertura, String filtroStatus,
                                               String campoOrdenacao, boolean crescente) {
        StringBuilder sb = new StringBuilder("SELECT p FROM PlanoConvenio p LEFT JOIN FETCH p.convenio c");
        Map<String, Object> params = new LinkedHashMap<>();
        List<String> condicoes = new ArrayList<>();

        if (filtroConvenioId != null) {
            condicoes.add("c.id = :idc");
            params.put("idc", filtroConvenioId);
        }
        if (filtroNome != null && !filtroNome.trim().isEmpty()) {
            condicoes.add("LOWER(p.nome) LIKE :fn");
            params.put("fn", "%" + filtroNome.trim().toLowerCase() + "%");
        }
        if (filtroCobertura != null && !filtroCobertura.trim().isEmpty()) {
            condicoes.add("p.tipoCobertura = :tc");
            params.put("tc", TipoCoberturaPlano.valueOf(filtroCobertura));
        }
        if (filtroStatus != null && !filtroStatus.trim().isEmpty()) {
            condicoes.add("p.status = :st");
            params.put("st", IndicativoStatus.valueOf(filtroStatus));
        }
        if (!condicoes.isEmpty()) {
            sb.append(" WHERE ").append(String.join(" AND ", condicoes));
        }
        sb.append(resolverOrdenacao(campoOrdenacao, crescente));

        TypedQuery<PlanoConvenio> q = em.createQuery(sb.toString(), PlanoConvenio.class);
        params.forEach(q::setParameter);
        return q.setFirstResult(primeiro).setMaxResults(tamanho).getResultList();
    }

    public long contarTotal(Long filtroConvenioId, String filtroNome,
                            String filtroCobertura, String filtroStatus) {
        StringBuilder sb = new StringBuilder("SELECT COUNT(p) FROM PlanoConvenio p LEFT JOIN p.convenio c");
        Map<String, Object> params = new LinkedHashMap<>();
        List<String> condicoes = new ArrayList<>();

        if (filtroConvenioId != null) {
            condicoes.add("c.id = :idc");
            params.put("idc", filtroConvenioId);
        }
        if (filtroNome != null && !filtroNome.trim().isEmpty()) {
            condicoes.add("LOWER(p.nome) LIKE :fn");
            params.put("fn", "%" + filtroNome.trim().toLowerCase() + "%");
        }
        if (filtroCobertura != null && !filtroCobertura.trim().isEmpty()) {
            condicoes.add("p.tipoCobertura = :tc");
            params.put("tc", TipoCoberturaPlano.valueOf(filtroCobertura));
        }
        if (filtroStatus != null && !filtroStatus.trim().isEmpty()) {
            condicoes.add("p.status = :st");
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
                "SELECT COUNT(p) FROM PlanoConvenio p WHERE p.status = :st",
                Long.class)
                .setParameter("st", IndicativoStatus.A)
                .getSingleResult();
    }

    public PlanoConvenio buscarPorNomeEConvenio(Long idConvenio, String nome) {
        if (nome == null || nome.trim().isEmpty()) return null;
        try {
            return em.createQuery(
                    "SELECT p FROM PlanoConvenio p WHERE p.convenio.id = :idc " +
                    "AND LOWER(p.nome) = LOWER(:nome)",
                    PlanoConvenio.class)
                    .setParameter("idc", idConvenio)
                    .setParameter("nome", nome.trim())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private String resolverOrdenacao(String campo, boolean crescente) {
        String dir = crescente ? "ASC" : "DESC";
        if ("nome".equals(campo))             return " ORDER BY p.nome " + dir;
        if ("convenio".equals(campo))         return " ORDER BY c.nome " + dir + ", p.nome ASC";
        if ("convenio.nome".equals(campo))    return " ORDER BY c.nome " + dir + ", p.nome ASC";
        if ("tipoCobertura".equals(campo))    return " ORDER BY p.tipoCobertura " + dir;
        if ("valorMensalidade".equals(campo)) return " ORDER BY p.valorMensalidade " + dir;
        if ("status".equals(campo))           return " ORDER BY p.status " + dir;
        if ("dataCadastro".equals(campo))     return " ORDER BY p.dataCadastro " + dir;
        return " ORDER BY c.nome ASC, p.nome ASC";
    }

    public PlanoConvenio salvar(PlanoConvenio plano) {
        em.persist(plano);
        em.flush();
        return plano;
    }

    public PlanoConvenio atualizar(PlanoConvenio plano) {
        PlanoConvenio merged = em.merge(plano);
        em.flush();
        return merged;
    }
}
