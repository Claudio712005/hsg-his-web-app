package br.com.hsg.service.facade.clinica;

import br.com.hsg.domain.entity.Enfermeiro;
import br.com.hsg.domain.entity.Especialidade;
import br.com.hsg.domain.entity.Medico;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ClinicaServiceFacade {

    Enfermeiro buscarEnfermeiroPorKeycloakId(String keycloakId);

    Medico buscarMedicoPorKeycloakId(String keycloakId);

    List<Especialidade> listarEspecialidadesAtivas();

    List<Medico> listarMedicosPaginado(int primeiro, int tamanho,
                                       String filtroNome, String filtroStatus,
                                       String campoOrdenacao, boolean crescente);

    long contarMedicos(String filtroNome, String filtroStatus);

    List<Enfermeiro> listarEnfermeirosPaginado(int primeiro, int tamanho,
                                               String filtroNome, String filtroSetor,
                                               String filtroStatus, String campoOrdenacao,
                                               boolean crescente);

    long contarEnfermeiros(String filtroNome, String filtroSetor, String filtroStatus);

    long contarEspecialidadesAtivas();

    List<Especialidade> listarEspecialidadesPaginado(int primeiro, int tamanho,
                                                     String filtroNome, String filtroArea,
                                                     String filtroStatus, String campoOrdenacao,
                                                     boolean crescente);

    long contarEspecialidades(String filtroNome, String filtroArea, String filtroStatus);
}
