package br.com.hsg.dao;

import br.com.hsg.domain.entity.ConsultaHistorico;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ConsultaHistoricoDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public ConsultaHistorico salvar(ConsultaHistorico h) {
        em.persist(h);
        return h;
    }

    public List<ConsultaHistorico> listarPorConsulta(Long idConsulta) {
        return em.createQuery(
                "SELECT h FROM ConsultaHistorico h " +
                "WHERE h.consulta.id = :idc " +
                "ORDER BY h.dataAcao DESC",
                ConsultaHistorico.class)
                .setParameter("idc", idConsulta)
                .getResultList();
    }
}
