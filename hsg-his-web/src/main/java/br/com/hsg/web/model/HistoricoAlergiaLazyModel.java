package br.com.hsg.web.model;

import br.com.hsg.domain.entity.Alergia;
import br.com.hsg.domain.enums.GravidadeAlergia;
import br.com.hsg.domain.enums.StatusAlergia;
import br.com.hsg.domain.enums.TipoAlergia;
import br.com.hsg.service.facade.paciente.AlergiaServiceFacade;
import br.com.hsg.web.dto.response.HistoricoAlergiaDTO;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistoricoAlergiaLazyModel extends LazyDataModel<HistoricoAlergiaDTO> {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final AlergiaServiceFacade service;

    private String           filtroPaciente;
    private TipoAlergia      filtroTipo;
    private GravidadeAlergia filtroGravidade;
    private StatusAlergia    filtroStatus;

    public HistoricoAlergiaLazyModel(AlergiaServiceFacade service) {
        this.service = service;
    }

    public void aplicarFiltros(String paciente, TipoAlergia tipo, GravidadeAlergia gravidade, StatusAlergia status) {
        this.filtroPaciente  = paciente;
        this.filtroTipo      = tipo;
        this.filtroGravidade = gravidade;
        this.filtroStatus    = status;
    }

    @Override
    public List<HistoricoAlergiaDTO> load(int first, int pageSize, String sortField,
                                          SortOrder sortOrder, Map<String, Object> filters) {
        setRowCount((int) service.contarHistorico(filtroPaciente, filtroTipo, filtroGravidade, filtroStatus));
        boolean ascending = sortOrder == null || sortOrder == SortOrder.ASCENDING;
        List<Alergia> alergias = service.listarHistorico(first, pageSize,
                filtroPaciente, filtroTipo, filtroGravidade, filtroStatus,
                sortField, ascending);
        List<HistoricoAlergiaDTO> dtos = new ArrayList<>();
        for (Alergia a : alergias) {
            dtos.add(toDTO(a));
        }
        return dtos;
    }

    private HistoricoAlergiaDTO toDTO(Alergia a) {
        HistoricoAlergiaDTO dto = new HistoricoAlergiaDTO();
        dto.setId(a.getId());
        dto.setPacienteId(a.getPaciente().getId());
        dto.setNomePaciente(a.getPaciente().getNomeCompleto());
        dto.setNomeAlergia(a.getNome());
        dto.setDescricao(a.getDescricao());
        dto.setTipoDescricao(a.getTipoAlergia() != null ? a.getTipoAlergia().getDescricao() : "—");
        dto.setGravidadeDescricao(a.getGravidadeAlergia() != null ? a.getGravidadeAlergia().getDescricao() : "—");
        dto.setGravidadeCssClass(gravCss(a.getGravidadeAlergia()));
        dto.setReacao(a.getReacao());
        dto.setDataUltimaReacao(a.getDataUltimaReacao() != null ? a.getDataUltimaReacao().format(FMT) : null);
        dto.setObservacao(a.getObservacao());
        dto.setObservacaoAvaliador(a.getObservacaoEnfermeiro());
        dto.setStatusDescricao(a.getStatusAlergia() != null ? a.getStatusAlergia().getDescricao() : "—");
        dto.setStatusCssClass(statusCss(a.getStatusAlergia()));
        dto.setDataCadastro(a.getDataCadastro() != null ? a.getDataCadastro().format(FMT) : "—");
        dto.setDataAvaliacao(a.getDataUltimaAlteracao() != null ? a.getDataUltimaAlteracao().format(FMT) : "—");
        return dto;
    }

    private String gravCss(GravidadeAlergia g) {
        if (g == null) return "";
        switch (g) {
            case A: return "chip-anafilatica";
            case G: return "chip-grave";
            case M: return "chip-moderada";
            case L: return "chip-leve";
            default: return "";
        }
    }

    private String statusCss(StatusAlergia s) {
        if (s == null) return "pendente";
        switch (s) {
            case APROVADA:  return "ativo";
            case REJEITADA: return "inativo";
            default:        return "pendente";
        }
    }
}
