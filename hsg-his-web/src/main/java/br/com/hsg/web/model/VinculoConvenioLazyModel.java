package br.com.hsg.web.model;

import br.com.hsg.domain.entity.PacienteConvenio;
import br.com.hsg.service.facade.admin.RelatorioConvenioServiceFacade;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class VinculoConvenioLazyModel extends LazyDataModel<PacienteConvenio> {

    private static final long serialVersionUID = 1L;

    private final RelatorioConvenioServiceFacade service;
    private String filtroPaciente;
    private Long filtroConvenioId;

    public VinculoConvenioLazyModel(RelatorioConvenioServiceFacade service) {
        this.service = service;
    }

    @Override
    public List<PacienteConvenio> load(int first, int pageSize, String sortField,
                                       SortOrder sortOrder, Map<String, Object> filters) {
        boolean crescente = sortOrder != SortOrder.DESCENDING;
        long total = service.contarVinculosAtivos(filtroPaciente, filtroConvenioId);
        setRowCount((int) total);
        if (total == 0) {
            return Collections.emptyList();
        }
        return service.listarVinculosAtivosPaginado(first, pageSize, filtroPaciente, filtroConvenioId,
                sortField, crescente);
    }

    public void aplicarFiltros(String filtroPaciente, Long filtroConvenioId) {
        this.filtroPaciente   = filtroPaciente;
        this.filtroConvenioId = filtroConvenioId;
    }

    public String getFiltroPaciente()         { return filtroPaciente; }
    public void setFiltroPaciente(String v)   { this.filtroPaciente = v; }
    public Long getFiltroConvenioId()         { return filtroConvenioId; }
    public void setFiltroConvenioId(Long v)   { this.filtroConvenioId = v; }
}
