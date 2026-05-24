package br.com.hsg.web.model;

import br.com.hsg.domain.entity.RegraCobertura;
import br.com.hsg.service.facade.admin.ConvenioServiceFacade;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RegraCoberturaLazyModel extends LazyDataModel<RegraCobertura> {

    private static final long serialVersionUID = 1L;

    private final ConvenioServiceFacade service;
    private Long filtroPlanoId;
    private String filtroProcedimento;
    private String filtroCategoria;
    private String filtroStatus;

    public RegraCoberturaLazyModel(ConvenioServiceFacade service) {
        this.service = service;
    }

    @Override
    public List<RegraCobertura> load(int first, int pageSize, String sortField,
                                      SortOrder sortOrder, Map<String, Object> filters) {
        boolean crescente = sortOrder != SortOrder.DESCENDING;
        long total = service.contarRegras(filtroPlanoId, filtroProcedimento, filtroCategoria, filtroStatus);
        setRowCount((int) total);
        if (total == 0) {
            return Collections.emptyList();
        }
        return service.listarRegrasPaginado(first, pageSize, filtroPlanoId, filtroProcedimento,
                filtroCategoria, filtroStatus, sortField, crescente);
    }

    public Long getFiltroPlanoId()           { return filtroPlanoId; }
    public void setFiltroPlanoId(Long v)     { this.filtroPlanoId = v; }
    public String getFiltroProcedimento()    { return filtroProcedimento; }
    public void setFiltroProcedimento(String v) { this.filtroProcedimento = v; }
    public String getFiltroCategoria()       { return filtroCategoria; }
    public void setFiltroCategoria(String v) { this.filtroCategoria = v; }
    public String getFiltroStatus()          { return filtroStatus; }
    public void setFiltroStatus(String v)    { this.filtroStatus = v; }
}
