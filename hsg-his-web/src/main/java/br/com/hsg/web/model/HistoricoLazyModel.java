package br.com.hsg.web.model;

import br.com.hsg.domain.entity.SolicitacaoAtualizacao;
import br.com.hsg.domain.enums.StatusSolicitacao;
import br.com.hsg.domain.enums.TipoSanguineoEnum;
import br.com.hsg.domain.enums.TipoSolicitacao;
import br.com.hsg.service.facade.paciente.SolicitacaoAtualizacaoServiceFacade;
import br.com.hsg.web.dto.response.SolicitacaoAtualizacaoDTO;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HistoricoLazyModel extends LazyDataModel<SolicitacaoAtualizacaoDTO> {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final SolicitacaoAtualizacaoServiceFacade service;
    private final Long pacienteId;
    private final List<TipoSolicitacao> tiposFiltro;

    public HistoricoLazyModel(SolicitacaoAtualizacaoServiceFacade service,
                              Long pacienteId,
                              List<TipoSolicitacao> tiposFiltro) {
        this.service = service;
        this.pacienteId = pacienteId;
        this.tiposFiltro = tiposFiltro;
    }

    @Override
    public List<SolicitacaoAtualizacaoDTO> load(int first, int pageSize,
                                                String sortField, SortOrder sortOrder, Map<String, Object> filters) {

        if (pacienteId == null) {
            setRowCount(0);
            return Collections.emptyList();
        }

        setRowCount((int) service.contarHistorico(pacienteId, tiposFiltro));

        List<SolicitacaoAtualizacao> entidades =
                service.consultarHistorico(pacienteId, first, pageSize, tiposFiltro);

        return entidades.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private SolicitacaoAtualizacaoDTO toDTO(SolicitacaoAtualizacao s) {
        SolicitacaoAtualizacaoDTO dto = new SolicitacaoAtualizacaoDTO();
        dto.setId(s.getId());
        dto.setDataCadastro(s.getDataCadastro() != null ? s.getDataCadastro().format(FMT) : "—");
        dto.setTipo(s.getTipoSolicitacao() != null ? s.getTipoSolicitacao().name() : null);
        dto.setTipoDescricao(s.getTipoSolicitacao() != null ? s.getTipoSolicitacao().getDescricao() : "—");
        dto.setStatusDescricao(s.getStatus() != null ? s.getStatus().getDescricao() : "—");
        dto.setStatusCssClass(cssStatus(s.getStatus()));
        dto.setMotivo(s.getMotivo() != null ? s.getMotivo() : "—");
        dto.setEnfermeiro(s.getEnfermeiro() != null ? s.getEnfermeiro().getNomeCompleto() : "Aguardando");
        dto.setCancelavel(s.getStatus() == StatusSolicitacao.P);
        dto.setMotivoCancelamento(s.getMotivoCancelamento());
        dto.setTipoCancelador(s.getTipoCancelador() != null ? s.getTipoCancelador().name() : null);
        dto.setDataCancelamento(s.getDataCancelamento() != null ? s.getDataCancelamento().format(FMT) : null);

        if (s.getTipoSolicitacao() == TipoSolicitacao.CADASTRAL) {
            dto.setNomePropostoCompleto(s.getNomeCompleto());
            dto.setEmailProposto(s.getEmail());
            dto.setTelefoneProposto(nvl(s.getTelefone()));
            dto.setSnapshotNome(nvl(s.getSnapshotNomeCompleto()));
            dto.setSnapshotEmail(nvl(s.getSnapshotEmail()));
            dto.setSnapshotTelefone(nvl(s.getSnapshotTelefone()));
            dto.setResumo(resumoCadastral(s));

        } else if (s.getTipoSolicitacao() == TipoSolicitacao.ENDERECO) {
            dto.setLogradouroProposto(nvl(s.getLogradouroProposto()));
            dto.setNumeroProposto(nvl(s.getNumeroProposto()));
            dto.setComplementoProposto(nvl(s.getComplementoProposto()));
            dto.setBairroProposto(nvl(s.getBairroProposto()));
            dto.setCidadeProposta(nvl(s.getCidadeProposta()));
            dto.setEstadoProposto(nvl(s.getEstadoProposto()));
            dto.setCepProposto(nvl(s.getCepProposto()));
            dto.setSnapshotLogradouro(nvl(s.getSnapshotLogradouro()));
            dto.setSnapshotNumero(nvl(s.getSnapshotNumero()));
            dto.setSnapshotComplemento(nvl(s.getSnapshotComplemento()));
            dto.setSnapshotBairro(nvl(s.getSnapshotBairro()));
            dto.setSnapshotCidade(nvl(s.getSnapshotCidade()));
            dto.setSnapshotEstado(nvl(s.getSnapshotEstado()));
            dto.setSnapshotCep(nvl(s.getSnapshotCep()));
            dto.setResumo(resumoEndereco(s));

        } else if (s.getTipoSolicitacao() == TipoSolicitacao.CLINICO) {
            dto.setPesoProposto(fmt(s.getPesoProposto(), "kg"));
            dto.setAlturaProposta(fmt(s.getAlturaProposta(), "m"));
            dto.setTipoSanguineoProposto(s.getTipoSanguineoProposto() != null ?
                    TipoSanguineoEnum.from(s.getTipoSanguineoProposto()).getDescricao() :
                    "Nenhum valor encontrado");
            dto.setSnapshotPeso(fmt(s.getSnapshotPeso(), "kg"));
            dto.setSnapshotAltura(fmt(s.getSnapshotAltura(), "m"));
            dto.setSnapshotTipoSanguineo(s.getSnapshotTipoSanguineo() != null ?
                    TipoSanguineoEnum.from(s.getSnapshotTipoSanguineo()).getDescricao() :
                    "Nenhum valor encontrado");
            dto.setResumo(resumoClinico(s));
        }

        return dto;
    }

    private String resumoCadastral(SolicitacaoAtualizacao s) {
        return "Nome: " + nvl(s.getSnapshotNomeCompleto()) + " → " + nvl(s.getNomeCompleto());
    }

    private String resumoEndereco(SolicitacaoAtualizacao s) {
        return "Cidade: " + nvl(s.getSnapshotCidade()) + " → " + nvl(s.getCidadeProposta());
    }

    private String resumoClinico(SolicitacaoAtualizacao s) {
        return "Peso: " + fmt(s.getSnapshotPeso(), "kg") + " → " + fmt(s.getPesoProposto(), "kg")
                + " | Altura: " + fmt(s.getSnapshotAltura(), "m") + " → " + fmt(s.getAlturaProposta(), "m");
    }

    private String cssStatus(StatusSolicitacao status) {
        if (status == null) return "pendente";
        switch (status) {
            case A:
                return "ativo";
            case R:
                return "inativo";
            case C:
                return "cancelado";
            default:
                return "pendente";
        }
    }

    private static String nvl(String v) {
        return v != null ? v : "Nenhum valor encontrado";
    }

    private static String fmt(Double v, String unit) {
        return v != null ? v + " " + unit : "Nenhum valor encontrado";
    }
}
