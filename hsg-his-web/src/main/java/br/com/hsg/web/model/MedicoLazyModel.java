package br.com.hsg.web.model;

import br.com.hsg.domain.entity.Medico;
import br.com.hsg.service.facade.clinica.ClinicaServiceFacade;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MedicoLazyModel extends LazyDataModel<Medico> {

    private static final long serialVersionUID = 1L;

    private final ClinicaServiceFacade service;
    private String filtroNome;
    private String filtroStatus;

    public MedicoLazyModel(ClinicaServiceFacade service) {
        this.service = service;
    }

    @Override
    public List<Medico> load(int first, int pageSize, String sortField,
                             SortOrder sortOrder, Map<String, Object> filters) {
        boolean crescente = sortOrder != SortOrder.DESCENDING;
        long total = service.contarMedicos(filtroNome, filtroStatus);
        setRowCount((int) total);
        if (total == 0) {
            return Collections.emptyList();
        }
        return service.listarMedicosPaginado(first, pageSize, filtroNome, filtroStatus, sortField, crescente);
    }

    public String getFiltroNome()   { return filtroNome; }
    public void setFiltroNome(String filtroNome)     { this.filtroNome = filtroNome; }
    public String getFiltroStatus() { return filtroStatus; }
    public void setFiltroStatus(String filtroStatus) { this.filtroStatus = filtroStatus; }
}
