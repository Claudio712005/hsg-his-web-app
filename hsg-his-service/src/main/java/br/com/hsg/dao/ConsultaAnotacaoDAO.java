package br.com.hsg.dao;

import br.com.hsg.domain.entity.ConsultaAnotacao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ConsultaAnotacaoDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public ConsultaAnotacao salvar(ConsultaAnotacao a) {
        em.persist(a);
        return a;
    }

    public List<ConsultaAnotacao> listarPorConsulta(Long idConsulta) {
        return em.createQuery(
                "SELECT a FROM ConsultaAnotacao a " +
                "WHERE a.consulta.id = :idc " +
                "ORDER BY a.dataCriacao DESC",
                ConsultaAnotacao.class)
                .setParameter("idc", idConsulta)
                .getResultList();
    }
}
