package br.com.hsg.web.model;

import br.com.hsg.domain.entity.Alergia;
import br.com.hsg.domain.enums.StatusAlergia;
import br.com.hsg.service.facade.paciente.AlergiaServiceFacade;
import br.com.hsg.web.dto.response.AlergiaResponseDTO;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AlergiaLazyModel extends LazyDataModel<AlergiaResponseDTO> {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final AlergiaServiceFacade service;
    private final Long pacienteId;

    public AlergiaLazyModel(AlergiaServiceFacade service, Long pacienteId) {
        this.service    = service;
        this.pacienteId = pacienteId;
    }

    @Override
    public List<AlergiaResponseDTO> load(int first, int pageSize,
                                         String sortField, SortOrder sortOrder,
                                         Map<String, Object> filters) {
        if (pacienteId == null) {
            setRowCount(0);
            return Collections.emptyList();
        }

        setRowCount((int) service.contarPorPaciente(pacienteId));

        List<Alergia> alergias = service.listarPorPaciente(pacienteId, first, pageSize);
        return alergias.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private AlergiaResponseDTO toDTO(Alergia a) {
        AlergiaResponseDTO dto = new AlergiaResponseDTO();
        dto.setId(a.getId());
        dto.setNome(a.getNome());
        dto.setDescricao(a.getDescricao());
        dto.setTipoDescricao(a.getTipoAlergia() != null ? a.getTipoAlergia().getDescricao() : "—");
        dto.setGravidadeDescricao(a.getGravidadeAlergia() != null ? a.getGravidadeAlergia().getDescricao() : "—");
        dto.setStatusDescricao(a.getStatusAlergia() != null ? a.getStatusAlergia().getDescricao() : "—");
        dto.setStatusCssClass(cssStatus(a.getStatusAlergia()));
        dto.setReacao(a.getReacao());
        dto.setObservacao(a.getObservacao());
        dto.setDataUltimaReacao(a.getDataUltimaReacao() != null ? a.getDataUltimaReacao().format(FMT) : null);
        dto.setDataCadastro(a.getDataCadastro() != null ? a.getDataCadastro().format(FMT) : "—");
        dto.setExcluivel(a.getStatusAlergia() == StatusAlergia.INFORMADA
                      || a.getStatusAlergia() == StatusAlergia.REJEITADA);
        return dto;
    }

    private String cssStatus(StatusAlergia status) {
        if (status == null) return "pendente";
        switch (status) {
            case APROVADA:  return "ativo";
            case REJEITADA: return "inativo";
            default:        return "pendente";
        }
    }
}
