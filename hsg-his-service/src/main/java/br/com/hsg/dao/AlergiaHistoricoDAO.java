package br.com.hsg.dao;

import br.com.hsg.domain.entity.AlergiaHistorico;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class AlergiaHistoricoDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public AlergiaHistorico salvar(AlergiaHistorico historico) {
        em.persist(historico);
        em.flush();
        return historico;
    }

    public List<AlergiaHistorico> listarPorAlergia(Long alergiaId) {
        return em.createQuery(
                "SELECT h FROM AlergiaHistorico h WHERE h.alergia.id = :alergiaId ORDER BY h.dataAcao DESC",
                AlergiaHistorico.class)
                .setParameter("alergiaId", alergiaId)
                .getResultList();
    }
}
