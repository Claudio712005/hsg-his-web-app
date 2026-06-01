package br.com.hsg.service.facade.paciente;

import br.com.hsg.domain.entity.AgendaMedicaSlot;
import br.com.hsg.domain.entity.Especialidade;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.entity.PacienteConvenio;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ConsultaBuscaServiceFacade {

    List<Especialidade> listarEspecialidadesAtivas();

    List<Medico> listarMedicosPorEspecialidade(Long idEspecialidade);

    List<AgendaMedicaSlot> listarHorariosLivresProximos(Long idEspecialidade, int diasFrente, Long idMedicoOpcional);

    PacienteConvenio buscarConvenioAtivo(Long idPaciente);
}
