package br.com.hsg.service.facade.paciente;

import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.service.dto.ResultadoFinanceiroConsulta;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ConsultaServiceFacade {

    ResultadoFinanceiroConsulta simular(Long idPaciente, Long idSlot, boolean usarConvenio);

    Consulta agendar(Long idPaciente, Long idEspecialidade, Long idSlot, boolean usarConvenio);

    void cancelarPeloPaciente(Long idConsulta, Long idPaciente, String motivo);

    List<Consulta> listarConsultasPaciente(Long idPaciente);

    List<Consulta> listarProximasPaciente(Long idPaciente, int limite);

    List<Consulta> listarProximasMedico(Long idMedico, int limite);
}
