package br.com.hsg.service.impl.paciente;

import br.com.hsg.dao.AgendaMedicaSlotDAO;
import br.com.hsg.dao.EspecialidadeDAO;
import br.com.hsg.dao.MedicoEspecialidadeDAO;
import br.com.hsg.dao.PacienteConvenioDAO;
import br.com.hsg.domain.entity.AgendaMedicaSlot;
import br.com.hsg.domain.entity.Especialidade;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.entity.PacienteConvenio;
import br.com.hsg.service.facade.paciente.ConsultaBuscaServiceFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class ConsultaBuscaServiceImpl implements ConsultaBuscaServiceFacade {

    @EJB private EspecialidadeDAO        especialidadeDAO;
    @EJB private MedicoEspecialidadeDAO  medicoEspecialidadeDAO;
    @EJB private AgendaMedicaSlotDAO     agendaMedicaSlotDAO;
    @EJB private PacienteConvenioDAO     pacienteConvenioDAO;

    @Override
    public List<Especialidade> listarEspecialidadesAtivas() {
        return especialidadeDAO.listarAtivas();
    }

    @Override
    public List<Medico> listarMedicosPorEspecialidade(Long idEspecialidade) {
        if (idEspecialidade == null) {
            throw new IllegalArgumentException("A especialidade é obrigatória.");
        }
        return medicoEspecialidadeDAO.listarMedicosPorEspecialidade(idEspecialidade);
    }

    @Override
    public List<AgendaMedicaSlot> listarHorariosLivresProximos(Long idEspecialidade, int diasFrente,
                                                                Long idMedicoOpcional) {
        if (idEspecialidade == null) {
            throw new IllegalArgumentException("A especialidade é obrigatória.");
        }
        if (diasFrente <= 0 || diasFrente > 180) {
            throw new IllegalArgumentException("Período em dias deve estar entre 1 e 180.");
        }
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fim    = LocalDate.now().plusDays(diasFrente).atStartOfDay();
        return agendaMedicaSlotDAO.listarLivresPorEspecialidadeData(
                idEspecialidade, inicio, fim, idMedicoOpcional);
    }

    @Override
    public PacienteConvenio buscarConvenioAtivo(Long idPaciente) {
        if (idPaciente == null) return null;
        return pacienteConvenioDAO.buscarAtivoPorPaciente(idPaciente);
    }
}
