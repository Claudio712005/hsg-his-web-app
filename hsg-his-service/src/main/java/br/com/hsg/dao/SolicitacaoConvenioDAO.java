package br.com.hsg.dao;

import br.com.hsg.domain.entity.SolicitacaoConvenio;
import br.com.hsg.domain.enums.StatusSolicitacao;

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
public class SolicitacaoConvenioDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public SolicitacaoConvenio buscarPorId(Long id) {
        try {
            return em.createQuery(
                    "SELECT s FROM SolicitacaoConvenio s " +
                    "JOIN FETCH s.paciente JOIN FETCH s.plano p JOIN FETCH p.convenio " +
                    "WHERE s.id = :id",
                    SolicitacaoConvenio.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean existePendentePorPaciente(Long idPaciente) {
        Long total = em.createQuery(
                "SELECT COUNT(s) FROM SolicitacaoConvenio s " +
                "WHERE s.paciente.id = :idp AND s.status = :st",
                Long.class)
                .setParameter("idp", idPaciente)
                .setParameter("st", StatusSolicitacao.P)
                .getSingleResult();
        return total != null && total > 0;
    }

    public List<SolicitacaoConvenio> listarPorPaciente(Long idPaciente) {
        return em.createQuery(
                "SELECT s FROM SolicitacaoConvenio s " +
                "JOIN FETCH s.plano p JOIN FETCH p.convenio " +
                "WHERE s.paciente.id = :idp ORDER BY s.dataCadastro DESC",
                SolicitacaoConvenio.class)
                .setParameter("idp", idPaciente)
                .getResultList();
    }

    public List<SolicitacaoConvenio> listarPaginado(int primeiro, int tamanho,
                                                     String filtroPaciente, String filtroStatus,
                                                     String campoOrdenacao, boolean crescente) {
        StringBuilder sb = new StringBuilder(
                "SELECT s FROM SolicitacaoConvenio s " +
                "JOIN FETCH s.paciente pac JOIN FETCH s.plano p JOIN FETCH p.convenio c");
        Map<String, Object> params = new LinkedHashMap<>();
        List<String> condicoes = new ArrayList<>();

        if (filtroPaciente != null && !filtroPaciente.trim().isEmpty()) {
            condicoes.add("(LOWER(pac.nome.primeiroNome) LIKE :fp OR LOWER(pac.nome.sobrenome) LIKE :fp)");
            params.put("fp", "%" + filtroPaciente.trim().toLowerCase() + "%");
        }
        if (filtroStatus != null && !filtroStatus.trim().isEmpty()) {
            condicoes.add("s.status = :st");
            params.put("st", StatusSolicitacao.valueOf(filtroStatus));
        }
        if (!condicoes.isEmpty()) {
            sb.append(" WHERE ").append(String.join(" AND ", condicoes));
        }
        sb.append(resolverOrdenacao(campoOrdenacao, crescente));

        TypedQuery<SolicitacaoConvenio> q = em.createQuery(sb.toString(), SolicitacaoConvenio.class);
        params.forEach(q::setParameter);
        return q.setFirstResult(primeiro).setMaxResults(tamanho).getResultList();
    }

    public long contarTotal(String filtroPaciente, String filtroStatus) {
        StringBuilder sb = new StringBuilder("SELECT COUNT(s) FROM SolicitacaoConvenio s JOIN s.paciente pac");
        Map<String, Object> params = new LinkedHashMap<>();
        List<String> condicoes = new ArrayList<>();

        if (filtroPaciente != null && !filtroPaciente.trim().isEmpty()) {
            condicoes.add("(LOWER(pac.nome.primeiroNome) LIKE :fp OR LOWER(pac.nome.sobrenome) LIKE :fp)");
            params.put("fp", "%" + filtroPaciente.trim().toLowerCase() + "%");
        }
        if (filtroStatus != null && !filtroStatus.trim().isEmpty()) {
            condicoes.add("s.status = :st");
            params.put("st", StatusSolicitacao.valueOf(filtroStatus));
        }
        if (!condicoes.isEmpty()) {
            sb.append(" WHERE ").append(String.join(" AND ", condicoes));
        }

        TypedQuery<Long> q = em.createQuery(sb.toString(), Long.class);
        params.forEach(q::setParameter);
        return q.getSingleResult();
    }

    public long contarPendentes() {
        return em.createQuery(
                "SELECT COUNT(s) FROM SolicitacaoConvenio s WHERE s.status = :st",
                Long.class)
                .setParameter("st", StatusSolicitacao.P)
                .getSingleResult();
    }

    private String resolverOrdenacao(String campo, boolean crescente) {
        String dir = crescente ? "ASC" : "DESC";
        if ("paciente".equals(campo))    return " ORDER BY pac.nome.primeiroNome " + dir + ", pac.nome.sobrenome ASC";
        if ("plano".equals(campo))       return " ORDER BY c.nome " + dir + ", p.nome ASC";
        if ("status".equals(campo))      return " ORDER BY s.status " + dir;
        if ("dataCadastro".equals(campo)) return " ORDER BY s.dataCadastro " + dir;
        return " ORDER BY s.dataCadastro DESC";
    }

    public SolicitacaoConvenio salvar(SolicitacaoConvenio s) {
        em.persist(s);
        em.flush();
        return s;
    }

    public SolicitacaoConvenio atualizar(SolicitacaoConvenio s) {
        SolicitacaoConvenio merged = em.merge(s);
        em.flush();
        return merged;
    }
}
