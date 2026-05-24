package br.com.hsg.dao;

import br.com.hsg.domain.entity.Alergia;
import br.com.hsg.domain.enums.GravidadeAlergia;
import br.com.hsg.domain.enums.StatusAlergia;
import br.com.hsg.domain.enums.TipoAlergia;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class AlergiaDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public Alergia salvar(Alergia alergia) {
        em.persist(alergia);
        em.flush();
        return alergia;
    }

    public Alergia atualizar(Alergia alergia) {
        Alergia merged = em.merge(alergia);
        em.flush();
        return merged;
    }

    public void excluir(Alergia alergia) {
        em.remove(em.contains(alergia) ? alergia : em.merge(alergia));
        em.flush();
    }

    public Alergia buscarPorId(Long id) {
        try {
            return em.createQuery(
                    "SELECT a FROM Alergia a WHERE a.id = :id", Alergia.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            return null;
        }
    }

    public List<Alergia> listarPorPaciente(Long pacienteId, int inicio, int tamanho) {
        return em.createQuery(
                "SELECT a FROM Alergia a WHERE a.paciente.id = :pacienteId ORDER BY a.dataCadastro DESC",
                Alergia.class)
                .setParameter("pacienteId", pacienteId)
                .setFirstResult(inicio)
                .setMaxResults(tamanho)
                .getResultList();
    }

    public long contarPorPaciente(Long pacienteId) {
        return em.createQuery(
                "SELECT COUNT(a) FROM Alergia a WHERE a.paciente.id = :pacienteId", Long.class)
                .setParameter("pacienteId", pacienteId)
                .getSingleResult();
    }

    public List<Alergia> listarParaAprovacao(int inicio, int tamanho) {
        return listarParaAprovacao(inicio, tamanho, null, null, null, null, true);
    }

    public long contarParaAprovacao() {
        return contarParaAprovacao(null, null, null);
    }

    public List<Alergia> listarParaAprovacao(int inicio, int tamanho,
                                             String filtroPaciente,
                                             TipoAlergia filtroTipo,
                                             GravidadeAlergia filtroGravidade) {
        return listarParaAprovacao(inicio, tamanho, filtroPaciente, filtroTipo, filtroGravidade, null, true);
    }

    public List<Alergia> listarParaAprovacao(int inicio, int tamanho,
                                             String filtroPaciente,
                                             TipoAlergia filtroTipo,
                                             GravidadeAlergia filtroGravidade,
                                             String sortField,
                                             boolean ascending) {
        Map<String, Object> params = new HashMap<>();
        params.put("st", StatusAlergia.INFORMADA);

        StringBuilder jpql = new StringBuilder(
                "SELECT a FROM Alergia a JOIN FETCH a.paciente p WHERE a.statusAlergia = :st");
        aplicarFiltros(jpql, params, filtroPaciente, filtroTipo, filtroGravidade);
        jpql.append(" ORDER BY ").append(resolveSortField(sortField, "a.dataCadastro"))
            .append(ascending ? " ASC" : " DESC");

        return executarList(jpql.toString(), params, inicio, tamanho);
    }

    public long contarParaAprovacao(String filtroPaciente,
                                    TipoAlergia filtroTipo,
                                    GravidadeAlergia filtroGravidade) {
        Map<String, Object> params = new HashMap<>();
        params.put("st", StatusAlergia.INFORMADA);

        StringBuilder jpql = new StringBuilder(
                "SELECT COUNT(a) FROM Alergia a JOIN a.paciente p WHERE a.statusAlergia = :st");
        aplicarFiltros(jpql, params, filtroPaciente, filtroTipo, filtroGravidade);

        return executarCount(jpql.toString(), params);
    }

    public List<Alergia> listarHistorico(int inicio, int tamanho,
                                         String filtroPaciente,
                                         TipoAlergia filtroTipo,
                                         GravidadeAlergia filtroGravidade,
                                         StatusAlergia filtroStatus,
                                         String sortField,
                                         boolean ascending) {
        Map<String, Object> params = new HashMap<>();

        StringBuilder jpql = new StringBuilder(
                "SELECT a FROM Alergia a JOIN FETCH a.paciente p WHERE ");
        aplicarFiltroStatusHistorico(jpql, params, filtroStatus);
        aplicarFiltros(jpql, params, filtroPaciente, filtroTipo, filtroGravidade);
        jpql.append(" ORDER BY ").append(resolveSortField(sortField, "a.dataUltimaAlteracao"))
            .append(ascending ? " ASC" : " DESC");

        return executarList(jpql.toString(), params, inicio, tamanho);
    }

    public long contarHistorico(String filtroPaciente,
                                TipoAlergia filtroTipo,
                                GravidadeAlergia filtroGravidade,
                                StatusAlergia filtroStatus) {
        Map<String, Object> params = new HashMap<>();

        StringBuilder jpql = new StringBuilder(
                "SELECT COUNT(a) FROM Alergia a JOIN a.paciente p WHERE ");
        aplicarFiltroStatusHistorico(jpql, params, filtroStatus);
        aplicarFiltros(jpql, params, filtroPaciente, filtroTipo, filtroGravidade);

        return executarCount(jpql.toString(), params);
    }

    private void aplicarFiltroStatusHistorico(StringBuilder jpql, Map<String, Object> params,
                                              StatusAlergia filtroStatus) {
        if (filtroStatus != null
                && (filtroStatus == StatusAlergia.APROVADA || filtroStatus == StatusAlergia.REJEITADA)) {
            jpql.append("a.statusAlergia = :st");
            params.put("st", filtroStatus);
        } else {
            jpql.append("a.statusAlergia IN (:stList)");
            params.put("stList", Arrays.asList(StatusAlergia.APROVADA, StatusAlergia.REJEITADA));
        }
    }

    private void aplicarFiltros(StringBuilder jpql, Map<String, Object> params,
                                String filtroPaciente,
                                TipoAlergia filtroTipo,
                                GravidadeAlergia filtroGravidade) {
        if (filtroPaciente != null && !filtroPaciente.trim().isEmpty()) {
            jpql.append(" AND LOWER(CONCAT(p.nome.primeiroNome, ' ', p.nome.sobrenome)) LIKE :nomePaciente");
            params.put("nomePaciente", "%" + filtroPaciente.trim().toLowerCase() + "%");
        }
        if (filtroTipo != null) {
            jpql.append(" AND a.tipoAlergia = :tipo");
            params.put("tipo", filtroTipo);
        }
        if (filtroGravidade != null) {
            jpql.append(" AND a.gravidadeAlergia = :gravidade");
            params.put("gravidade", filtroGravidade);
        }
    }

    private String resolveSortField(String sortField, String defaultField) {
        if (sortField == null || sortField.isEmpty()) return defaultField;
        switch (sortField) {
            case "nomePaciente":        return "CONCAT(p.nome.primeiroNome, ' ', p.nome.sobrenome)";
            case "nomeAlergia":         return "a.nome";
            case "tipoAlergia":         return "a.tipoAlergia";
            case "gravidadeAlergia":    return "a.gravidadeAlergia";
            case "dataCadastro":        return "a.dataCadastro";
            case "dataUltimaAlteracao": return "a.dataUltimaAlteracao";
            case "statusAlergia":       return "a.statusAlergia";
            default:                    return defaultField;
        }
    }

    private List<Alergia> executarList(String jpql, Map<String, Object> params, int inicio, int tamanho) {
        TypedQuery<Alergia> query = em.createQuery(jpql, Alergia.class);
        for (Map.Entry<String, Object> e : params.entrySet()) {
            query.setParameter(e.getKey(), e.getValue());
        }
        return query.setFirstResult(inicio).setMaxResults(tamanho).getResultList();
    }

    private long executarCount(String jpql, Map<String, Object> params) {
        Query query = em.createQuery(jpql);
        for (Map.Entry<String, Object> e : params.entrySet()) {
            query.setParameter(e.getKey(), e.getValue());
        }
        return (Long) query.getSingleResult();
    }
}
