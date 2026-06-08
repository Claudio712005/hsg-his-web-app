package br.com.hsg.service.facade.clinica;

import br.com.hsg.domain.enums.TipoResponsavel;
import br.com.hsg.service.dto.prontuario.ProntuarioDTO;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ProntuarioServiceFacade {

    final class PacienteBuscaDTO {
        public final Long   id;
        public final String nomeCompleto;
        public PacienteBuscaDTO(Long id, String nomeCompleto) {
            this.id = id;
            this.nomeCompleto = nomeCompleto;
        }
    }

    ProntuarioDTO montarParaPaciente(Long idPaciente, Long idSolicitante, TipoResponsavel tipoSolicitante);

    List<PacienteBuscaDTO> buscarPacientes(String termo, Long idSolicitante,
                                            TipoResponsavel tipoSolicitante, int limite);
}
