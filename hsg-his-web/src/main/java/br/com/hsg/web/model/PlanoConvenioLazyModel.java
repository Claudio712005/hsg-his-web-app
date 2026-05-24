package br.com.hsg.web.model;

import br.com.hsg.domain.entity.PlanoConvenio;
import br.com.hsg.service.facade.admin.ConvenioServiceFacade;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PlanoConvenioLazyModel extends LazyDataModel<PlanoConvenio> {

    private static final long serialVersionUID = 1L;

    private final ConvenioServiceFacade service;
    private Long filtroConvenioId;
    private String filtroNome;
    private String filtroCobertura;
    private String filtroStatus;

    public PlanoConvenioLazyModel(ConvenioServiceFacade service) {
        this.service = service;
    }

    @Override
    public List<PlanoConvenio> load(int first, int pageSize, String sortField,
                                     SortOrder sortOrder, Map<String, Object> filters) {
        boolean crescente = sortOrder != SortOrder.DESCENDING;
        long total = service.contarPlanos(filtroConvenioId, filtroNome, filtroCobertura, filtroStatus);
        setRowCount((int) total);
        if (total == 0) {
            return Collections.emptyList();
        }
        return service.listarPlanosPaginado(first, pageSize, filtroConvenioId, filtroNome,
                filtroCobertura, filtroStatus, sortField, crescente);
    }

    public Long getFiltroConvenioId()       { return filtroConvenioId; }
    public void setFiltroConvenioId(Long v) { this.filtroConvenioId = v; }
    public String getFiltroNome()           { return filtroNome; }
    public void setFiltroNome(String v)     { this.filtroNome = v; }
    public String getFiltroCobertura()      { return filtroCobertura; }
    public void setFiltroCobertura(String v){ this.filtroCobertura = v; }
    public String getFiltroStatus()         { return filtroStatus; }
    public void setFiltroStatus(String v)   { this.filtroStatus = v; }
}
