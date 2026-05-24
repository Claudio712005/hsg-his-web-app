package br.com.hsg.web.model;

import br.com.hsg.domain.entity.Enfermeiro;
import br.com.hsg.service.facade.clinica.ClinicaServiceFacade;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EnfermeiroLazyModel extends LazyDataModel<Enfermeiro> {

    private static final long serialVersionUID = 1L;

    private final ClinicaServiceFacade service;
    private String filtroNome;
    private String filtroSetor;
    private String filtroStatus;

    public EnfermeiroLazyModel(ClinicaServiceFacade service) {
        this.service = service;
    }

    @Override
    public List<Enfermeiro> load(int first, int pageSize, String sortField,
                                 SortOrder sortOrder, Map<String, Object> filters) {
        boolean crescente = sortOrder != SortOrder.DESCENDING;
        long total = service.contarEnfermeiros(filtroNome, filtroSetor, filtroStatus);
        setRowCount((int) total);
        if (total == 0) {
            return Collections.emptyList();
        }
        return service.listarEnfermeirosPaginado(first, pageSize, filtroNome, filtroSetor, filtroStatus, sortField, crescente);
    }

    public String getFiltroNome()   { return filtroNome; }
    public void setFiltroNome(String filtroNome)     { this.filtroNome = filtroNome; }
    public String getFiltroSetor()  { return filtroSetor; }
    public void setFiltroSetor(String filtroSetor)   { this.filtroSetor = filtroSetor; }
    public String getFiltroStatus() { return filtroStatus; }
    public void setFiltroStatus(String filtroStatus) { this.filtroStatus = filtroStatus; }
}
