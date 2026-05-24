package br.com.hsg.dao;

import br.com.hsg.domain.entity.Medico;
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
public class MedicoDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public List<Medico> listarPaginado(int primeiro, int tamanho,
                                       String filtroNome, String filtroStatus,
                                       String campoOrdenacao, boolean crescente) {
        StringBuilder sb = new StringBuilder(
            "SELECT m FROM Medico m JOIN FETCH m.contaUsuario LEFT JOIN FETCH m.especialidade e"
        );
        Map<String, Object> params = new LinkedHashMap<>();
        List<String> condicoes = new ArrayList<>();

        if (filtroNome != null && !filtroNome.trim().isEmpty()) {
            condicoes.add("(LOWER(m.nome.primeiroNome) LIKE :fn OR LOWER(m.nome.sobrenome) LIKE :fn)");
            params.put("fn", "%" + filtroNome.trim().toLowerCase() + "%");
        }
        if (filtroStatus != null && !filtroStatus.trim().isEmpty()) {
            condicoes.add("m.status = :st");
            params.put("st", IndicativoStatus.valueOf(filtroStatus));
        }
        if (!condicoes.isEmpty()) {
            sb.append(" WHERE ").append(String.join(" AND ", condicoes));
        }
        sb.append(resolverOrdenacao(campoOrdenacao, crescente));

        TypedQuery<Medico> q = em.createQuery(sb.toString(), Medico.class);
        params.forEach(q::setParameter);
        return q.setFirstResult(primeiro).setMaxResults(tamanho).getResultList();
    }

    public long contarTotal(String filtroNome, String filtroStatus) {
        StringBuilder sb = new StringBuilder("SELECT COUNT(m) FROM Medico m");
        Map<String, Object> params = new LinkedHashMap<>();
        List<String> condicoes = new ArrayList<>();

        if (filtroNome != null && !filtroNome.trim().isEmpty()) {
            condicoes.add("(LOWER(m.nome.primeiroNome) LIKE :fn OR LOWER(m.nome.sobrenome) LIKE :fn)");
            params.put("fn", "%" + filtroNome.trim().toLowerCase() + "%");
        }
        if (filtroStatus != null && !filtroStatus.trim().isEmpty()) {
            condicoes.add("m.status = :st");
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
        if ("nome".equals(campo))                return " ORDER BY m.nome.primeiroNome " + dir + ", m.nome.sobrenome " + dir;
        if ("crm.numero".equals(campo))          return " ORDER BY m.crm.numero " + dir;
        if ("especialidade.nome".equals(campo))  return " ORDER BY e.nome " + dir;
        if ("status".equals(campo))              return " ORDER BY m.status " + dir;
        if ("dataCadastro".equals(campo))        return " ORDER BY m.dataCadastro " + dir;
        return " ORDER BY m.nome.primeiroNome ASC, m.nome.sobrenome ASC";
    }

    public Medico buscarPorId(Long id) {
        try {
            return em.createQuery(
                    "SELECT m FROM Medico m JOIN FETCH m.contaUsuario WHERE m.id = :id",
                    Medico.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Medico salvar(Medico medico) {
        em.persist(medico);
        em.flush();
        return medico;
    }

    public Medico buscarPorKeycloakId(String keycloakId) {
        try {
            return em.createQuery(
                    "SELECT m FROM Medico m JOIN FETCH m.contaUsuario c LEFT JOIN FETCH m.especialidade WHERE c.keycloakId = :kcId",
                    Medico.class)
                    .setParameter("kcId", keycloakId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Medico> listarAtivos() {
        return em.createQuery(
                "SELECT m FROM Medico m LEFT JOIN FETCH m.especialidade WHERE m.status = :st " +
                "ORDER BY m.nome.primeiroNome ASC, m.nome.sobrenome ASC",
                Medico.class)
                .setParameter("st", IndicativoStatus.A)
                .getResultList();
    }
}
