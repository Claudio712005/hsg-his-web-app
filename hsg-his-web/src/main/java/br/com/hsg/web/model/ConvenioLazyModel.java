package br.com.hsg.web.model;

import br.com.hsg.domain.entity.Convenio;
import br.com.hsg.service.facade.admin.ConvenioServiceFacade;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConvenioLazyModel extends LazyDataModel<Convenio> {

    private static final long serialVersionUID = 1L;

    private final ConvenioServiceFacade service;
    private String filtroNome;
    private String filtroStatus;

    public ConvenioLazyModel(ConvenioServiceFacade service) {
        this.service = service;
    }

    @Override
    public List<Convenio> load(int first, int pageSize, String sortField,
                                SortOrder sortOrder, Map<String, Object> filters) {
        boolean crescente = sortOrder != SortOrder.DESCENDING;
        long total = service.contarConvenios(filtroNome, filtroStatus);
        setRowCount((int) total);
        if (total == 0) {
            return Collections.emptyList();
        }
        return service.listarConveniosPaginado(first, pageSize, filtroNome, filtroStatus,
                sortField, crescente);
    }

    public String getFiltroNome()   { return filtroNome; }
    public void setFiltroNome(String v)   { this.filtroNome = v; }
    public String getFiltroStatus() { return filtroStatus; }
    public void setFiltroStatus(String v) { this.filtroStatus = v; }
}
