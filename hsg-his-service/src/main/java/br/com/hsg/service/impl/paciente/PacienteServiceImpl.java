package br.com.hsg.service.impl.paciente;

import br.com.hsg.dao.PacienteDAO;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.service.facade.paciente.PacienteServiceFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class PacienteServiceImpl implements PacienteServiceFacade {

    @EJB
    private PacienteDAO pacienteDAO;

    @Override
    public Paciente buscarPorKeycloakId(String keycloakId) {
        return pacienteDAO.buscarPorKeycloakId(keycloakId);
    }

    @Override
    public Paciente buscarPorId(Long id) {
        return pacienteDAO.buscarPorId(id);
    }
}
