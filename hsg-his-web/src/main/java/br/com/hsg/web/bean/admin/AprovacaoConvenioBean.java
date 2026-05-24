package br.com.hsg.web.bean.admin;

import br.com.hsg.domain.entity.RegraCobertura;
import br.com.hsg.domain.entity.SolicitacaoConvenio;
import br.com.hsg.domain.enums.StatusSolicitacao;
import br.com.hsg.service.facade.admin.AprovacaoConvenioServiceFacade;
import br.com.hsg.web.bean.session.BeanSessao;
import br.com.hsg.web.model.SolicitacaoConvenioLazyModel;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

@ViewScoped
@Named("aprovacaoConvenioBean")
public class AprovacaoConvenioBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AprovacaoConvenioBean.class.getName());
    private static final DateTimeFormatter FMT_DT   = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Inject private BeanSessao beanSessao;
    @EJB    private AprovacaoConvenioServiceFacade aprovacaoService;

    private SolicitacaoConvenioLazyModel model;
    private SolicitacaoConvenio solicitacaoSelecionada;
    private String motivoRejeicao;

    private String filtroPaciente;
    private String filtroStatus;
    private long totalPendentes;

    @PostConstruct
    public void init() {
        model = new SolicitacaoConvenioLazyModel(aprovacaoService);
        filtroStatus = StatusSolicitacao.P.name();
        model.aplicarFiltros(null, filtroStatus);
        atualizarTotais();
    }

    private void atualizarTotais() {
        this.totalPendentes = aprovacaoService.contarPendentes();
    }

    public void aplicarFiltros() {
        model.aplicarFiltros(filtroPaciente, filtroStatus);
    }

    public void limparFiltros() {
        this.filtroPaciente = null;
        this.filtroStatus   = null;
        model.aplicarFiltros(null, null);
    }

    public void selecionar(SolicitacaoConvenio s) {
        this.solicitacaoSelecionada = s;
        this.motivoRejeicao = null;
    }

    public void prepararRejeicao(SolicitacaoConvenio s) {
        this.solicitacaoSelecionada = s;
        this.motivoRejeicao = null;
    }

    public void aprovar() {
        if (solicitacaoSelecionada == null) return;
        try {
            aprovacaoService.aprovar(solicitacaoSelecionada.getId(), idAdminLogado());
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Solicitação aprovada. Convênio do paciente atualizado.");
            recarregar();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            adicionarMensagem(FacesMessage.SEVERITY_WARN, ex.getMessage());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "[AprovacaoConvenioBean] Erro ao aprovar", ex);
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro inesperado ao aprovar solicitação.");
        }
    }

    public void rejeitar() {
        if (solicitacaoSelecionada == null) return;
        if (motivoRejeicao == null || motivoRejeicao.trim().isEmpty()) {
            adicionarMensagem(FacesMessage.SEVERITY_WARN, "Informe o motivo da rejeição.");
            return;
        }
        try {
            aprovacaoService.rejeitar(solicitacaoSelecionada.getId(), idAdminLogado(), motivoRejeicao);
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Solicitação rejeitada.");
            recarregar();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            adicionarMensagem(FacesMessage.SEVERITY_WARN, ex.getMessage());
        }
    }

    private void recarregar() {
        this.solicitacaoSelecionada = null;
        this.motivoRejeicao = null;
        model.aplicarFiltros(filtroPaciente, filtroStatus);
        atualizarTotais();
    }

    private Long idAdminLogado() {
        return beanSessao.getAdmin() != null ? beanSessao.getAdmin().getId() : null;
    }

    public StatusSolicitacao[] getStatusOpcoes() {
        return StatusSolicitacao.values();
    }

    public List<RegraCobertura> getRegrasDoPlanoSelecionado() {
        if (solicitacaoSelecionada == null || solicitacaoSelecionada.getPlano() == null) {
            return Collections.emptyList();
        }
        return aprovacaoService.listarRegrasDoPlano(solicitacaoSelecionada.getPlano().getId());
    }

    public String formatarData(java.time.LocalDateTime dt) {
        return dt != null ? dt.format(FMT_DT) : "—";
    }

    public String formatarDataSimples(LocalDate d) {
        return d != null ? d.format(FMT_DATA) : "—";
    }

    private void adicionarMensagem(FacesMessage.Severity severity, String mensagem) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, mensagem, null));
    }

    public SolicitacaoConvenioLazyModel getModel()        { return model; }
    public SolicitacaoConvenio getSolicitacaoSelecionada(){ return solicitacaoSelecionada; }
    public void setSolicitacaoSelecionada(SolicitacaoConvenio s) { this.solicitacaoSelecionada = s; }
    public String getMotivoRejeicao()                     { return motivoRejeicao; }
    public void setMotivoRejeicao(String v)               { this.motivoRejeicao = v; }
    public String getFiltroPaciente()                     { return filtroPaciente; }
    public void setFiltroPaciente(String v)               { this.filtroPaciente = v; }
    public String getFiltroStatus()                       { return filtroStatus; }
    public void setFiltroStatus(String v)                 { this.filtroStatus = v; }
    public long getTotalPendentes()                       { return totalPendentes; }
}
