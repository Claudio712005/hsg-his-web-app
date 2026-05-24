package br.com.hsg.web.bean.paciente;

import br.com.hsg.domain.entity.AlergiaHistorico;
import br.com.hsg.domain.enums.GravidadeAlergia;
import br.com.hsg.domain.enums.TipoAlergia;
import br.com.hsg.service.facade.paciente.AlergiaServiceFacade;
import br.com.hsg.web.bean.session.BeanSessao;
import br.com.hsg.web.dto.response.AlergiaResponseDTO;
import br.com.hsg.web.dto.response.PacienteResponseDTO;
import br.com.hsg.web.model.AlergiaLazyModel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ViewScoped
@Named("alergiaBean")
@Slf4j
public class AlergiaBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @EJB
    private AlergiaServiceFacade alergiaService;

    @Inject
    private BeanSessao beanSessao;

    @Getter
    private AlergiaLazyModel model;

    @Getter
    @Setter
    private AlergiaResponseDTO alergiaVisualizacao;

    @Getter
    @Setter
    private String nomeNovaAlergia;
    @Getter
    @Setter
    private String descricaoNovaAlergia;
    @Getter
    @Setter
    private TipoAlergia tipoNovaAlergia;
    @Getter
    @Setter
    private GravidadeAlergia gravidadeNovaAlergia;
    @Getter
    @Setter
    private String reacaoNovaAlergia;
    @Getter
    @Setter
    private String observacaoNovaAlergia;

    private Long idAlergiaParaExcluir;

    @Getter
    private List<TipoAlergia>      tiposAlergia;
    @Getter
    private List<GravidadeAlergia> gravidadesAlergia;

    @PostConstruct
    public void init() {
        tiposAlergia      = Arrays.asList(TipoAlergia.values());
        gravidadesAlergia = Arrays.asList(GravidadeAlergia.values());

        Long pacienteId = pacienteId();
        log.debug("Inicializando AlergiaBean para pacienteId={}", pacienteId);
        model = new AlergiaLazyModel(alergiaService, pacienteId);
    }

    public void registrarAlergia() {
        try {
            Long pacienteId = requererPacienteId();
            log.info("Registrando alergia para pacienteId={}", pacienteId);
            alergiaService.informarAlergia(
                    pacienteId,
                    pacienteId,
                    nomeNovaAlergia,
                    descricaoNovaAlergia,
                    tipoNovaAlergia,
                    gravidadeNovaAlergia,
                    reacaoNovaAlergia,
                    null,
                    observacaoNovaAlergia
            );
            limparFormNovaAlergia();
            model.setRowCount(0);
            log.info("Alergia registrada para pacienteId={}", pacienteId);
            mensagem(FacesMessage.SEVERITY_INFO, "Alergia registrada",
                    "Alergia informada com sucesso e aguarda análise.");
        } catch (IllegalArgumentException e) {
            log.warn("Dados inválidos ao registrar alergia: {}", e.getMessage());
            mensagem(FacesMessage.SEVERITY_WARN, "Dados inválidos", e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao registrar alergia", e);
            mensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Não foi possível registrar a alergia. Tente novamente.");
        }
    }

    public void prepararExclusao(Long id) {
        this.idAlergiaParaExcluir = id;
    }

    public void confirmarExclusao() {
        try {
            Long pacienteId = requererPacienteId();
            Long alergiaId  = idAlergiaParaExcluir;
            log.info("Excluindo alergia={} para pacienteId={}", alergiaId, pacienteId);
            alergiaService.excluirAlergia(alergiaId, pacienteId);
            idAlergiaParaExcluir = null;
            model.setRowCount(0);
            log.info("Alergia={} excluída para pacienteId={}", alergiaId, pacienteId);
            mensagem(FacesMessage.SEVERITY_INFO, "Excluída", "Alergia excluída com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Não foi possível excluir alergia={}: {}", idAlergiaParaExcluir, e.getMessage());
            mensagem(FacesMessage.SEVERITY_WARN, "Não foi possível excluir", e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao excluir alergia={}", idAlergiaParaExcluir, e);
            mensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Não foi possível excluir. Tente novamente.");
        }
    }

    public void selecionarAlergia(AlergiaResponseDTO dto) {
        this.alergiaVisualizacao = dto;
        if (dto != null) {
            try {
                List<AlergiaHistorico> hist = alergiaService.buscarHistorico(dto.getId());
                List<AlergiaResponseDTO.AlergiaHistoricoDTO> histDTOs = hist.stream().map(h -> {
                    AlergiaResponseDTO.AlergiaHistoricoDTO d = new AlergiaResponseDTO.AlergiaHistoricoDTO();
                    d.setAcaoDescricao(h.getAcao() != null ? h.getAcao().getDescricao() : "—");
                    d.setNomeSnap(h.getNomeSnap());
                    d.setTipoSnap(h.getTipoSnap() != null ? h.getTipoSnap().getDescricao() : "—");
                    d.setGravidadeSnap(h.getGravidadeSnap() != null ? h.getGravidadeSnap().getDescricao() : "—");
                    d.setStatusSnap(h.getStatusSnap() != null ? h.getStatusSnap().getDescricao() : "—");
                    d.setDataAcao(h.getDataAcao() != null ? h.getDataAcao().format(FMT) : "—");
                    return d;
                }).collect(Collectors.toList());
                this.alergiaVisualizacao.setHistorico(histDTOs);
            } catch (Exception e) {
                log.warn("Não foi possível carregar histórico da alergia={}", dto.getId(), e);
                this.alergiaVisualizacao.setHistorico(Collections.emptyList());
            }
        }
    }

    private void limparFormNovaAlergia() {
        nomeNovaAlergia        = null;
        descricaoNovaAlergia   = null;
        tipoNovaAlergia        = null;
        gravidadeNovaAlergia   = null;
        reacaoNovaAlergia      = null;
        observacaoNovaAlergia  = null;
    }

    private Long pacienteId() {
        PacienteResponseDTO p = beanSessao.getPaciente();
        return p != null ? p.getId() : null;
    }

    private Long requererPacienteId() {
        Long id = pacienteId();
        if (id == null) throw new IllegalArgumentException("Sessão inválida. Faça login novamente.");
        return id;
    }

    private void mensagem(FacesMessage.Severity sev, String resumo, String detalhe) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(sev, resumo, detalhe));
    }
}
