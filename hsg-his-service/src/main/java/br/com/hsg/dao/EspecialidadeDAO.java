package br.com.hsg.dao;

import br.com.hsg.domain.entity.Especialidade;
import br.com.hsg.domain.enums.IndicativoStatus;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class EspecialidadeDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public Especialidade buscarPorId(Long id) {
        return em.find(Especialidade.class, id);
    }

    public Especialidade buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) return null;
        String nomeNorm = nome.trim();

        List<Especialidade> exatos = em.createQuery(
                "SELECT e FROM Especialidade e WHERE LOWER(e.nome) = LOWER(:nome)",
                Especialidade.class)
                .setParameter("nome", nomeNorm)
                .getResultList();
        if (!exatos.isEmpty()) return exatos.get(0);

        String nomeNormLower = nomeNorm.toLowerCase();
        List<Especialidade> todas = em.createQuery(
                "SELECT e FROM Especialidade e WHERE e.status = :st ORDER BY LENGTH(e.nome) DESC",
                Especialidade.class)
                .setParameter("st", IndicativoStatus.A)
                .getResultList();
        for (Especialidade e : todas) {
            if (nomeNormLower.startsWith(e.getNome().trim().toLowerCase())) {
                return e;
            }
        }

        return null;
    }

    public List<Especialidade> listarAtivas() {
        return em.createQuery(
                "SELECT e FROM Especialidade e WHERE e.status = :st ORDER BY e.nome ASC",
                Especialidade.class)
                .setParameter("st", IndicativoStatus.A)
                .getResultList();
    }

    public List<Especialidade> listarPaginado(int primeiro, int tamanho,
                                              String filtroNome, String filtroArea,
                                              String filtroStatus, String campoOrdenacao,
                                              boolean crescente) {
        StringBuilder sb = new StringBuilder("SELECT e FROM Especialidade e");
        Map<String, Object> params = new LinkedHashMap<>();
        List<String> condicoes = new ArrayList<>();

        if (filtroNome != null && !filtroNome.trim().isEmpty()) {
            condicoes.add("LOWER(e.nome) LIKE :fn");
            params.put("fn", "%" + filtroNome.trim().toLowerCase() + "%");
        }
        if (filtroArea != null && !filtroArea.trim().isEmpty()) {
            condicoes.add("e.area = :ar");
            params.put("ar", filtroArea.trim().toUpperCase());
        }
        if (filtroStatus != null && !filtroStatus.trim().isEmpty()) {
            condicoes.add("e.status = :st");
            params.put("st", IndicativoStatus.valueOf(filtroStatus));
        }
        if (!condicoes.isEmpty()) {
            sb.append(" WHERE ").append(String.join(" AND ", condicoes));
        }
        sb.append(resolverOrdenacao(campoOrdenacao, crescente));

        TypedQuery<Especialidade> q = em.createQuery(sb.toString(), Especialidade.class);
        params.forEach(q::setParameter);
        return q.setFirstResult(primeiro).setMaxResults(tamanho).getResultList();
    }

    public long contarTotal(String filtroNome, String filtroArea, String filtroStatus) {
        StringBuilder sb = new StringBuilder("SELECT COUNT(e) FROM Especialidade e");
        Map<String, Object> params = new LinkedHashMap<>();
        List<String> condicoes = new ArrayList<>();

        if (filtroNome != null && !filtroNome.trim().isEmpty()) {
            condicoes.add("LOWER(e.nome) LIKE :fn");
            params.put("fn", "%" + filtroNome.trim().toLowerCase() + "%");
        }
        if (filtroArea != null && !filtroArea.trim().isEmpty()) {
            condicoes.add("e.area = :ar");
            params.put("ar", filtroArea.trim().toUpperCase());
        }
        if (filtroStatus != null && !filtroStatus.trim().isEmpty()) {
            condicoes.add("e.status = :st");
            params.put("st", IndicativoStatus.valueOf(filtroStatus));
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
        if ("nome".equals(campo))        return " ORDER BY e.nome " + dir;
        if ("area".equals(campo))        return " ORDER BY e.area " + dir;
        if ("status".equals(campo))      return " ORDER BY e.status " + dir;
        if ("dataCadastro".equals(campo)) return " ORDER BY e.dataCadastro " + dir;
        return " ORDER BY e.nome ASC";
    }

    public void salvar(Especialidade especialidade) {
        em.persist(especialidade);
    }
}
