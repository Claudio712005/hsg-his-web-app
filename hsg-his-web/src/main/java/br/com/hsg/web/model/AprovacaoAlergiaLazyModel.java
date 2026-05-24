package br.com.hsg.web.model;

import br.com.hsg.domain.entity.Alergia;
import br.com.hsg.domain.enums.GravidadeAlergia;
import br.com.hsg.domain.enums.TipoAlergia;
import br.com.hsg.service.facade.paciente.AlergiaServiceFacade;
import br.com.hsg.web.dto.response.AprovacaoAlergiaDTO;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AprovacaoAlergiaLazyModel extends LazyDataModel<AprovacaoAlergiaDTO> {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final AlergiaServiceFacade service;

    private String           filtroPaciente;
    private TipoAlergia      filtroTipo;
    private GravidadeAlergia filtroGravidade;

    public AprovacaoAlergiaLazyModel(AlergiaServiceFacade service) {
        this.service = service;
    }

    public void aplicarFiltros(String paciente, TipoAlergia tipo, GravidadeAlergia gravidade) {
        this.filtroPaciente  = paciente;
        this.filtroTipo      = tipo;
        this.filtroGravidade = gravidade;
    }

    @Override
    public List<AprovacaoAlergiaDTO> load(int first, int pageSize, String sortField,
                                          SortOrder sortOrder, Map<String, Object> filters) {
        setRowCount((int) service.contarParaAprovacao(filtroPaciente, filtroTipo, filtroGravidade));
        boolean ascending = sortOrder == null || sortOrder == SortOrder.ASCENDING;
        List<Alergia> alergias = service.listarParaAprovacao(first, pageSize,
                filtroPaciente, filtroTipo, filtroGravidade, sortField, ascending);
        List<AprovacaoAlergiaDTO> dtos = new ArrayList<>();
        for (Alergia a : alergias) {
            dtos.add(toDTO(a));
        }
        return dtos;
    }

    private AprovacaoAlergiaDTO toDTO(Alergia a) {
        AprovacaoAlergiaDTO dto = new AprovacaoAlergiaDTO();
        dto.setId(a.getId());
        dto.setPacienteId(a.getPaciente().getId());
        dto.setNomePaciente(a.getPaciente().getNomeCompleto());
        dto.setNomeAlergia(a.getNome());
        dto.setDescricao(a.getDescricao());
        dto.setTipoDescricao(a.getTipoAlergia() != null ? a.getTipoAlergia().getDescricao() : "—");
        dto.setGravidadeDescricao(a.getGravidadeAlergia() != null ? a.getGravidadeAlergia().getDescricao() : "—");
        dto.setGravidadeCssClass(gravCss(a));
        dto.setReacao(a.getReacao());
        dto.setDataUltimaReacao(a.getDataUltimaReacao() != null ? a.getDataUltimaReacao().format(FMT) : null);
        dto.setObservacao(a.getObservacao());
        dto.setDataCadastro(a.getDataCadastro() != null ? a.getDataCadastro().format(FMT) : "—");
        return dto;
    }

    private String gravCss(Alergia a) {
        if (a.getGravidadeAlergia() == null) return "";
        switch (a.getGravidadeAlergia()) {
            case A: return "chip-anafilatica";
            case G: return "chip-grave";
            case M: return "chip-moderada";
            case L: return "chip-leve";
            default: return "";
        }
    }
}
