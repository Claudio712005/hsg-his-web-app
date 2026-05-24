package br.com.hsg.dao;

import br.com.hsg.domain.entity.MedicaoPaciente;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.domain.entity.TipoSanguineo;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class PainelPacienteDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public Paciente buscarPorId(Long id) {
        try {
            return em.createQuery(
                    "SELECT p FROM Paciente p " +
                    "JOIN FETCH p.contaUsuario " +
                    "WHERE p.id = :id",
                    Paciente.class
            ).setParameter("id", id)
             .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public MedicaoPaciente buscarUltimaMedicao(Long pacienteId) {
        List<MedicaoPaciente> result = em.createQuery(
                "SELECT m FROM MedicaoPaciente m " +
                "WHERE m.paciente.id = :id " +
                "ORDER BY m.dataMedicao DESC",
                MedicaoPaciente.class
        ).setParameter("id", pacienteId)
         .setMaxResults(1)
         .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public TipoSanguineo buscarUltimoTipoSanguineo(Long pacienteId) {
        List<TipoSanguineo> result = em.createQuery(
                "SELECT t FROM TipoSanguineo t " +
                "WHERE t.paciente.id = :id " +
                "ORDER BY t.dataCadastroPaciente DESC",
                TipoSanguineo.class
        ).setParameter("id", pacienteId)
         .setMaxResults(1)
         .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }
}