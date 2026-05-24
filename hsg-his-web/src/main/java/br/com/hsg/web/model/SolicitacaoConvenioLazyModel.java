package br.com.hsg.web.model;

import br.com.hsg.domain.entity.SolicitacaoConvenio;
import br.com.hsg.service.facade.admin.AprovacaoConvenioServiceFacade;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SolicitacaoConvenioLazyModel extends LazyDataModel<SolicitacaoConvenio> {

    private static final long serialVersionUID = 1L;

    private final AprovacaoConvenioServiceFacade service;
    private String filtroPaciente;
    private String filtroStatus;

    public SolicitacaoConvenioLazyModel(AprovacaoConvenioServiceFacade service) {
        this.service = service;
    }

    @Override
    public List<SolicitacaoConvenio> load(int first, int pageSize, String sortField,
                                          SortOrder sortOrder, Map<String, Object> filters) {
        boolean crescente = sortOrder != SortOrder.DESCENDING;
        long total = service.contarTotal(filtroPaciente, filtroStatus);
        setRowCount((int) total);
        if (total == 0) {
            return Collections.emptyList();
        }
        return service.listarPaginado(first, pageSize, filtroPaciente, filtroStatus, sortField, crescente);
    }

    public void aplicarFiltros(String filtroPaciente, String filtroStatus) {
        this.filtroPaciente = filtroPaciente;
        this.filtroStatus   = filtroStatus;
    }

    public String getFiltroPaciente()   { return filtroPaciente; }
    public void setFiltroPaciente(String v)   { this.filtroPaciente = v; }
    public String getFiltroStatus()     { return filtroStatus; }
    public void setFiltroStatus(String v)     { this.filtroStatus = v; }
}
