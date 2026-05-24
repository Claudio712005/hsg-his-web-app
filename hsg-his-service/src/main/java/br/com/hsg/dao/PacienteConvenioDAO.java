package br.com.hsg.dao;

import br.com.hsg.domain.entity.PacienteConvenio;
import br.com.hsg.domain.enums.IndicativoStatus;
import br.com.hsg.domain.enums.TipoCoberturaPlano;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class PacienteConvenioDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public PacienteConvenio buscarAtivoPorPaciente(Long idPaciente) {
        try {
            return em.createQuery(
                    "SELECT pc FROM PacienteConvenio pc " +
                    "JOIN FETCH pc.plano p JOIN FETCH p.convenio " +
                    "WHERE pc.paciente.id = :idp AND pc.status = :st",
                    PacienteConvenio.class)
                    .setParameter("idp", idPaciente)
                    .setParameter("st", IndicativoStatus.A)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<PacienteConvenio> listarHistoricoPorPaciente(Long idPaciente) {
        return em.createQuery(
                "SELECT pc FROM PacienteConvenio pc " +
                "JOIN FETCH pc.plano p JOIN FETCH p.convenio " +
                "WHERE pc.paciente.id = :idp ORDER BY pc.dataAdesao DESC",
                PacienteConvenio.class)
                .setParameter("idp", idPaciente)
                .getResultList();
    }

    public List<Object[]> contarPorConvenio() {
        return contarPorConvenio(null);
    }

    public List<Object[]> contarPorConvenio(TipoCoberturaPlano filtroCobertura) {
        StringBuilder sb = new StringBuilder(
                "SELECT c.nome, COUNT(pc), COUNT(DISTINCT p.id), COALESCE(SUM(p.valorMensalidade), 0) " +
                "FROM PacienteConvenio pc JOIN pc.plano p JOIN p.convenio c WHERE pc.status = :st");
        if (filtroCobertura != null) {
            sb.append(" AND p.tipoCobertura = :tc");
        }
        sb.append(" GROUP BY c.nome ORDER BY COUNT(pc) DESC, c.nome ASC");

        TypedQuery<Object[]> q = em.createQuery(sb.toString(), Object[].class)
                .setParameter("st", IndicativoStatus.A);
        if (filtroCobertura != null) {
            q.setParameter("tc", filtroCobertura);
        }
        return q.getResultList();
    }

    public List<Object[]> contarPorPlanoDoConvenio(Long idConvenio) {
        return em.createQuery(
                "SELECT p.nome, COUNT(pc) FROM PacienteConvenio pc " +
                "JOIN pc.plano p JOIN p.convenio c " +
                "WHERE pc.status = :st AND c.id = :idc " +
                "GROUP BY p.nome ORDER BY COUNT(pc) DESC, p.nome ASC",
                Object[].class)
                .setParameter("st", IndicativoStatus.A)
                .setParameter("idc", idConvenio)
                .getResultList();
    }

    public List<Object[]> contarPorPlanoGlobal(int limite) {
        return em.createQuery(
                "SELECT p.nome, COUNT(pc) FROM PacienteConvenio pc JOIN pc.plano p " +
                "WHERE pc.status = :st GROUP BY p.nome ORDER BY COUNT(pc) DESC, p.nome ASC",
                Object[].class)
                .setParameter("st", IndicativoStatus.A)
                .setMaxResults(limite)
                .getResultList();
    }

    public BigDecimal somaReceitaMensalAtiva() {
        Object r = em.createQuery(
                "SELECT COALESCE(SUM(p.valorMensalidade), 0) FROM PacienteConvenio pc JOIN pc.plano p " +
                "WHERE pc.status = :st")
                .setParameter("st", IndicativoStatus.A)
                .getSingleResult();
        if (r == null) return BigDecimal.ZERO;
        return (r instanceof BigDecimal) ? (BigDecimal) r : new BigDecimal(r.toString());
    }

    public long contarVinculosAtivos(String filtroPaciente, Long filtroConvenioId) {
        StringBuilder sb = new StringBuilder(
                "SELECT COUNT(pc) FROM PacienteConvenio pc JOIN pc.plano p JOIN p.convenio c " +
                "JOIN pc.paciente pac WHERE pc.status = :st");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("st", IndicativoStatus.A);

        if (filtroPaciente != null && !filtroPaciente.trim().isEmpty()) {
            sb.append(" AND (LOWER(pac.nome.primeiroNome) LIKE :fp OR LOWER(pac.nome.sobrenome) LIKE :fp)");
            params.put("fp", "%" + filtroPaciente.trim().toLowerCase() + "%");
        }
        if (filtroConvenioId != null) {
            sb.append(" AND c.id = :idc");
            params.put("idc", filtroConvenioId);
        }

        TypedQuery<Long> q = em.createQuery(sb.toString(), Long.class);
        params.forEach(q::setParameter);
        return q.getSingleResult();
    }

    public List<PacienteConvenio> listarVinculosAtivosPaginado(int primeiro, int tamanho,
                                                               String filtroPaciente, Long filtroConvenioId,
                                                               String campoOrdenacao, boolean crescente) {
        StringBuilder sb = new StringBuilder(
                "SELECT pc FROM PacienteConvenio pc " +
                "JOIN FETCH pc.paciente pac JOIN FETCH pc.plano p JOIN FETCH p.convenio c " +
                "WHERE pc.status = :st");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("st", IndicativoStatus.A);

        if (filtroPaciente != null && !filtroPaciente.trim().isEmpty()) {
            sb.append(" AND (LOWER(pac.nome.primeiroNome) LIKE :fp OR LOWER(pac.nome.sobrenome) LIKE :fp)");
            params.put("fp", "%" + filtroPaciente.trim().toLowerCase() + "%");
        }
        if (filtroConvenioId != null) {
            sb.append(" AND c.id = :idc");
            params.put("idc", filtroConvenioId);
        }
        sb.append(resolverOrdenacao(campoOrdenacao, crescente));

        TypedQuery<PacienteConvenio> q = em.createQuery(sb.toString(), PacienteConvenio.class);
        params.forEach(q::setParameter);
        return q.setFirstResult(primeiro).setMaxResults(tamanho).getResultList();
    }

    private String resolverOrdenacao(String campo, boolean crescente) {
        String dir = crescente ? "ASC" : "DESC";
        if ("paciente".equals(campo)) return " ORDER BY pac.nome.primeiroNome " + dir + ", pac.nome.sobrenome ASC";
        if ("convenio".equals(campo)) return " ORDER BY c.nome " + dir + ", p.nome ASC";
        if ("plano".equals(campo))    return " ORDER BY p.nome " + dir;
        if ("dataAdesao".equals(campo)) return " ORDER BY pc.dataAdesao " + dir;
        return " ORDER BY pc.dataAdesao DESC";
    }

    public PacienteConvenio salvar(PacienteConvenio pc) {
        em.persist(pc);
        em.flush();
        return pc;
    }

    public PacienteConvenio atualizar(PacienteConvenio pc) {
        PacienteConvenio merged = em.merge(pc);
        em.flush();
        return merged;
    }
}
