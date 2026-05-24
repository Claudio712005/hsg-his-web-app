package br.com.hsg.web.model;

import br.com.hsg.domain.entity.Especialidade;
import br.com.hsg.service.facade.clinica.ClinicaServiceFacade;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EspecialidadeLazyModel extends LazyDataModel<Especialidade> {

    private static final long serialVersionUID = 1L;

    private final ClinicaServiceFacade service;
    private String filtroNome;
    private String filtroArea;
    private String filtroStatus;

    public EspecialidadeLazyModel(ClinicaServiceFacade service) {
        this.service = service;
    }

    @Override
    public List<Especialidade> load(int first, int pageSize, String sortField,
                                    SortOrder sortOrder, Map<String, Object> filters) {
        boolean crescente = sortOrder != SortOrder.DESCENDING;
        long total = service.contarEspecialidades(filtroNome, filtroArea, filtroStatus);
        setRowCount((int) total);
        if (total == 0) {
            return Collections.emptyList();
        }
        return service.listarEspecialidadesPaginado(first, pageSize, filtroNome, filtroArea, filtroStatus, sortField, crescente);
    }

    public String getFiltroNome()   { return filtroNome; }
    public void setFiltroNome(String v)   { this.filtroNome = v; }
    public String getFiltroArea()   { return filtroArea; }
    public void setFiltroArea(String v)   { this.filtroArea = v; }
    public String getFiltroStatus() { return filtroStatus; }
    public void setFiltroStatus(String v) { this.filtroStatus = v; }
}
