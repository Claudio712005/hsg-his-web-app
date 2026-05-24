package br.com.hsg.service.impl.clinica;

import br.com.hsg.dao.EnfermeiroDAO;
import br.com.hsg.dao.EspecialidadeDAO;
import br.com.hsg.dao.MedicoDAO;
import br.com.hsg.domain.entity.Enfermeiro;
import br.com.hsg.domain.entity.Especialidade;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.service.facade.clinica.ClinicaServiceFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class ClinicaServiceImpl implements ClinicaServiceFacade {

    @EJB private EnfermeiroDAO   enfermeiroDAO;
    @EJB private MedicoDAO       medicoDAO;
    @EJB private EspecialidadeDAO especialidadeDAO;

    @Override
    public Enfermeiro buscarEnfermeiroPorKeycloakId(String keycloakId) {
        return enfermeiroDAO.buscarPorKeycloakId(keycloakId);
    }

    @Override
    public Medico buscarMedicoPorKeycloakId(String keycloakId) {
        return medicoDAO.buscarPorKeycloakId(keycloakId);
    }

    @Override
    public List<Especialidade> listarEspecialidadesAtivas() {
        return especialidadeDAO.listarAtivas();
    }

    @Override
    public List<Medico> listarMedicosPaginado(int primeiro, int tamanho,
                                              String filtroNome, String filtroStatus,
                                              String campoOrdenacao, boolean crescente) {
        return medicoDAO.listarPaginado(primeiro, tamanho, filtroNome, filtroStatus, campoOrdenacao, crescente);
    }

    @Override
    public long contarMedicos(String filtroNome, String filtroStatus) {
        return medicoDAO.contarTotal(filtroNome, filtroStatus);
    }

    @Override
    public List<Enfermeiro> listarEnfermeirosPaginado(int primeiro, int tamanho,
                                                      String filtroNome, String filtroSetor,
                                                      String filtroStatus, String campoOrdenacao,
                                                      boolean crescente) {
        return enfermeiroDAO.listarPaginado(primeiro, tamanho, filtroNome, filtroSetor, filtroStatus, campoOrdenacao, crescente);
    }

    @Override
    public long contarEnfermeiros(String filtroNome, String filtroSetor, String filtroStatus) {
        return enfermeiroDAO.contarTotal(filtroNome, filtroSetor, filtroStatus);
    }

    @Override
    public long contarEspecialidadesAtivas() {
        return especialidadeDAO.listarAtivas().size();
    }

    @Override
    public List<Especialidade> listarEspecialidadesPaginado(int primeiro, int tamanho,
                                                            String filtroNome, String filtroArea,
                                                            String filtroStatus, String campoOrdenacao,
                                                            boolean crescente) {
        return especialidadeDAO.listarPaginado(primeiro, tamanho, filtroNome, filtroArea, filtroStatus, campoOrdenacao, crescente);
    }

    @Override
    public long contarEspecialidades(String filtroNome, String filtroArea, String filtroStatus) {
        return especialidadeDAO.contarTotal(filtroNome, filtroArea, filtroStatus);
    }
}
