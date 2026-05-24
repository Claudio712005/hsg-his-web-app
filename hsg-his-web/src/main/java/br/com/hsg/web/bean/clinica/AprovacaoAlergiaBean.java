package br.com.hsg.web.bean.clinica;

import br.com.hsg.domain.enums.GravidadeAlergia;
import br.com.hsg.domain.enums.StatusAlergia;
import br.com.hsg.domain.enums.TipoAlergia;
import br.com.hsg.service.facade.paciente.AlergiaServiceFacade;
import br.com.hsg.web.bean.session.BeanSessao;
import br.com.hsg.web.dto.response.AprovacaoAlergiaDTO;
import br.com.hsg.web.dto.response.HistoricoAlergiaDTO;
import br.com.hsg.web.model.AprovacaoAlergiaLazyModel;
import br.com.hsg.web.model.HistoricoAlergiaLazyModel;
import br.com.hsg.web.util.JSFUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@ViewScoped
@Named("aprovacaoAlergiaBean")
public class AprovacaoAlergiaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject private AlergiaServiceFacade alergiaService;
    @Inject private BeanSessao           beanSessao;

    private AprovacaoAlergiaLazyModel model;
    private AprovacaoAlergiaDTO        alergiaSelecionada;
    private String                     observacaoAprovador;

    private String filtroPaciente;
    private String filtroTipo;
    private String filtroGravidade;

    private HistoricoAlergiaLazyModel historicoModel;
    private HistoricoAlergiaDTO       historicoSelecionado;

    private String filtroHistoricoPaciente;
    private String filtroHistoricoTipo;
    private String filtroHistoricoGravidade;
    private String filtroHistoricoStatus;

    @PostConstruct
    public void init() {
        model = new AprovacaoAlergiaLazyModel(alergiaService);
        model.aplicarFiltros(null, null, null);

        historicoModel = new HistoricoAlergiaLazyModel(alergiaService);
        historicoModel.aplicarFiltros(null, null, null, null);
    }

    public void aplicarFiltros() {
        model.aplicarFiltros(filtroPaciente, tipoSelecionado(filtroTipo), gravidadeSelecionada(filtroGravidade));
    }

    public void limparFiltros() {
        this.filtroPaciente  = null;
        this.filtroTipo      = null;
        this.filtroGravidade = null;
        model.aplicarFiltros(null, null, null);
    }

    public void aplicarFiltrosHistorico() {
        historicoModel.aplicarFiltros(
                filtroHistoricoPaciente,
                tipoSelecionado(filtroHistoricoTipo),
                gravidadeSelecionada(filtroHistoricoGravidade),
                statusSelecionado(filtroHistoricoStatus));
    }

    public void limparFiltrosHistorico() {
        this.filtroHistoricoPaciente  = null;
        this.filtroHistoricoTipo      = null;
        this.filtroHistoricoGravidade = null;
        this.filtroHistoricoStatus    = null;
        historicoModel.aplicarFiltros(null, null, null, null);
    }

    public void prepararVisualizacao(AprovacaoAlergiaDTO dto) {
        this.alergiaSelecionada  = dto;
        this.observacaoAprovador = null;
    }

    public void prepararAprovacao(AprovacaoAlergiaDTO dto) {
        this.alergiaSelecionada  = dto;
        this.observacaoAprovador = null;
    }

    public void prepararRejeicao(AprovacaoAlergiaDTO dto) {
        this.alergiaSelecionada  = dto;
        this.observacaoAprovador = null;
    }

    public void prepararVisualizacaoHistorico(HistoricoAlergiaDTO dto) {
        this.historicoSelecionado = dto;
    }

    public void confirmarAprovacao() {
        if (alergiaSelecionada == null) return;
        try {
            alergiaService.aprovarAlergia(
                    alergiaSelecionada.getId(),
                    beanSessao.getUsuarioClinica().getId(),
                    observacaoAprovador);
            JSFUtil.adicionarMensagem(null, FacesMessage.SEVERITY_INFO,
                    "Alergia aprovada com sucesso.");
            recarregar();
        } catch (IllegalStateException e) {
            JSFUtil.adicionarMensagem(null, FacesMessage.SEVERITY_WARN, e.getMessage());
        }
    }

    public void confirmarRejeicao() {
        if (alergiaSelecionada == null) return;
        if (observacaoAprovador == null || observacaoAprovador.trim().isEmpty()) {
            JSFUtil.adicionarMensagem(null, FacesMessage.SEVERITY_WARN,
                    "Informe o motivo da rejeição.");
            return;
        }
        try {
            alergiaService.rejeitarAlergia(
                    alergiaSelecionada.getId(),
                    beanSessao.getUsuarioClinica().getId(),
                    observacaoAprovador);
            JSFUtil.adicionarMensagem(null, FacesMessage.SEVERITY_INFO,
                    "Alergia rejeitada.");
            recarregar();
        } catch (IllegalStateException e) {
            JSFUtil.adicionarMensagem(null, FacesMessage.SEVERITY_WARN, e.getMessage());
        }
    }

    public long getTotalPendentes() {
        return alergiaService.contarParaAprovacao(
                filtroPaciente, tipoSelecionado(filtroTipo), gravidadeSelecionada(filtroGravidade));
    }

    public TipoAlergia[] getTiposAlergia()             { return TipoAlergia.values(); }
    public GravidadeAlergia[] getGravidadesAlergia()   { return GravidadeAlergia.values(); }

    public StatusAlergia[] getStatusHistorico() {
        return new StatusAlergia[]{ StatusAlergia.APROVADA, StatusAlergia.REJEITADA };
    }

    private TipoAlergia tipoSelecionado(String v) {
        return (v == null || v.isEmpty()) ? null : TipoAlergia.from(v);
    }

    private GravidadeAlergia gravidadeSelecionada(String v) {
        return (v == null || v.isEmpty()) ? null : GravidadeAlergia.from(v);
    }

    private StatusAlergia statusSelecionado(String v) {
        return (v == null || v.isEmpty()) ? null : StatusAlergia.valueOf(v);
    }

    private void recarregar() {
        alergiaSelecionada  = null;
        observacaoAprovador = null;
        model.aplicarFiltros(filtroPaciente, tipoSelecionado(filtroTipo), gravidadeSelecionada(filtroGravidade));
        historicoModel.aplicarFiltros(
                filtroHistoricoPaciente,
                tipoSelecionado(filtroHistoricoTipo),
                gravidadeSelecionada(filtroHistoricoGravidade),
                statusSelecionado(filtroHistoricoStatus));
    }

    public AprovacaoAlergiaLazyModel getModel()                  { return model; }
    public AprovacaoAlergiaDTO getAlergiaSelecionada()           { return alergiaSelecionada; }
    public void setAlergiaSelecionada(AprovacaoAlergiaDTO a)     { this.alergiaSelecionada = a; }
    public String getObservacaoAprovador()                       { return observacaoAprovador; }
    public void setObservacaoAprovador(String obs)               { this.observacaoAprovador = obs; }

    public String getFiltroPaciente()                            { return filtroPaciente; }
    public void setFiltroPaciente(String v)                      { this.filtroPaciente = v; }
    public String getFiltroTipo()                                { return filtroTipo; }
    public void setFiltroTipo(String v)                          { this.filtroTipo = v; }
    public String getFiltroGravidade()                           { return filtroGravidade; }
    public void setFiltroGravidade(String v)                     { this.filtroGravidade = v; }

    public HistoricoAlergiaLazyModel getHistoricoModel()                 { return historicoModel; }
    public HistoricoAlergiaDTO getHistoricoSelecionado()                 { return historicoSelecionado; }
    public void setHistoricoSelecionado(HistoricoAlergiaDTO h)           { this.historicoSelecionado = h; }
    public String getFiltroHistoricoPaciente()                           { return filtroHistoricoPaciente; }
    public void setFiltroHistoricoPaciente(String v)                     { this.filtroHistoricoPaciente = v; }
    public String getFiltroHistoricoTipo()                               { return filtroHistoricoTipo; }
    public void setFiltroHistoricoTipo(String v)                         { this.filtroHistoricoTipo = v; }
    public String getFiltroHistoricoGravidade()                          { return filtroHistoricoGravidade; }
    public void setFiltroHistoricoGravidade(String v)                    { this.filtroHistoricoGravidade = v; }
    public String getFiltroHistoricoStatus()                             { return filtroHistoricoStatus; }
    public void setFiltroHistoricoStatus(String v)                       { this.filtroHistoricoStatus = v; }
}
