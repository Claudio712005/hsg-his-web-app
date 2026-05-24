package br.com.hsg.dao;

import br.com.hsg.domain.entity.Endereco;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class EnderecoDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public Endereco buscarPorPaciente(Long pacienteId) {
        List<Endereco> resultado = em.createQuery(
                "SELECT e FROM Endereco e WHERE e.paciente.id = :pacienteId",
                Endereco.class
        ).setParameter("pacienteId", pacienteId)
         .setMaxResults(1)
         .getResultList();
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    public Endereco salvarOuAtualizar(Endereco endereco) {
        if (endereco.getId() == null) {
            em.persist(endereco);
        } else {
            endereco = em.merge(endereco);
        }
        em.flush();
        return endereco;
    }
}
