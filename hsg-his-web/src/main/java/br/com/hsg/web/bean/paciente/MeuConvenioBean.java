package br.com.hsg.web.bean.paciente;

import br.com.hsg.domain.entity.Convenio;
import br.com.hsg.domain.entity.PacienteConvenio;
import br.com.hsg.domain.entity.PlanoConvenio;
import br.com.hsg.domain.entity.RegraCobertura;
import br.com.hsg.domain.entity.SolicitacaoConvenio;
import br.com.hsg.domain.enums.TipoTitularidade;
import br.com.hsg.service.facade.paciente.ConvenioPacienteServiceFacade;
import br.com.hsg.web.bean.session.BeanSessao;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ViewScoped
@Named("meuConvenioBean")
public class MeuConvenioBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(MeuConvenioBean.class.getName());
    private static final DateTimeFormatter FMT_DT   = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Inject private BeanSessao beanSessao;
    @EJB    private ConvenioPacienteServiceFacade convenioPacienteService;

    private PacienteConvenio convenioAtivo;
    private List<PacienteConvenio> historico;
    private List<SolicitacaoConvenio> solicitacoes;
    private boolean possuiPendente;

    private Long       formConvenioId;
    private Long       formPlanoId;
    private String     formNumeroCarteirinha;
    private Date       formDataValidade;
    private String     formTitularidade;
    private String     formMotivo;

    @PostConstruct
    public void init() {
        carregar();
        novaSolicitacao();
    }

    private void carregar() {
        Long idPaciente = idPacienteLogado();
        if (idPaciente == null) {
            this.convenioAtivo  = null;
            this.historico      = Collections.emptyList();
            this.solicitacoes   = Collections.emptyList();
            this.possuiPendente = false;
            return;
        }
        this.convenioAtivo  = convenioPacienteService.buscarConvenioAtivo(idPaciente);
        this.historico      = convenioPacienteService.listarHistorico(idPaciente);
        this.solicitacoes   = convenioPacienteService.listarSolicitacoes(idPaciente);
        this.possuiPendente = convenioPacienteService.possuiSolicitacaoPendente(idPaciente);
    }

    private Long idPacienteLogado() {
        return beanSessao.getPaciente() != null ? beanSessao.getPaciente().getId() : null;
    }

    public void novaSolicitacao() {
        this.formConvenioId         = null;
        this.formPlanoId            = null;
        this.formNumeroCarteirinha  = null;
        this.formDataValidade       = null;
        this.formTitularidade       = TipoTitularidade.TITULAR.getValor();
        this.formMotivo             = null;
    }

    public void solicitar() {
        Long idPaciente = idPacienteLogado();
        if (idPaciente == null) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Sessão de paciente inválida.");
            return;
        }
        try {
            TipoTitularidade tipo = formTitularidade != null
                    ? TipoTitularidade.fromValor(formTitularidade) : TipoTitularidade.TITULAR;
            LocalDate dataValidade = formDataValidade != null
                    ? formDataValidade.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
            convenioPacienteService.solicitarConvenio(idPaciente, formPlanoId, formNumeroCarteirinha,
                    dataValidade, tipo, formMotivo);
            adicionarMensagem(FacesMessage.SEVERITY_INFO,
                    "Solicitação enviada. Aguarde a aprovação da administração.");
            carregar();
            novaSolicitacao();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "[MeuConvenioBean] Erro ao solicitar convênio", ex);
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro inesperado ao enviar solicitação.");
        }
    }

    public void cancelarSolicitacao(Long idSolicitacao) {
        Long idPaciente = idPacienteLogado();
        if (idPaciente == null) return;
        try {
            convenioPacienteService.cancelarSolicitacao(idSolicitacao, idPaciente);
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Solicitação cancelada.");
            carregar();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        }
    }

    public List<Convenio> getConveniosParaSelect() {
        return convenioPacienteService.listarConveniosAtivos();
    }

    public List<PlanoConvenio> getPlanosDoConvenioSelecionado() {
        if (formConvenioId == null) {
            return Collections.emptyList();
        }
        return convenioPacienteService.listarPlanosAtivosPorConvenio(formConvenioId);
    }

    public void aoMudarConvenio() {
        this.formPlanoId = null;
    }

    public PlanoConvenio getPlanoSelecionado() {
        return convenioPacienteService.buscarPlano(formPlanoId);
    }

    public List<RegraCobertura> getRegrasDoPlanoSelecionado() {
        if (formPlanoId == null) {
            return Collections.emptyList();
        }
        return convenioPacienteService.listarRegrasDoPlano(formPlanoId);
    }

    public List<RegraCobertura> getRegrasConvenioAtivo() {
        if (convenioAtivo == null || convenioAtivo.getPlano() == null) {
            return Collections.emptyList();
        }
        return convenioPacienteService.listarRegrasDoPlano(convenioAtivo.getPlano().getId());
    }

    public TipoTitularidade[] getTiposTitularidade() {
        return TipoTitularidade.values();
    }

    public String descreverCobertura(RegraCobertura r) {
        if (r == null) return "—";
        return r.isCoberto() ? "Coberto" : "Não coberto";
    }

    public String situacaoCarenciaLabel(RegraCobertura r) {
        if (r == null) return "—";
        if (!r.isCoberto()) return "Não coberto";
        if (convenioAtivo == null || convenioAtivo.getDataAdesao() == null) return "—";
        int dias = r.getCarenciaDias() != null ? r.getCarenciaDias() : 0;
        LocalDate liberacao = convenioAtivo.getDataAdesao().toLocalDate().plusDays(dias);
        if (!LocalDate.now().isBefore(liberacao)) {
            return "Liberado";
        }
        return "Em carência até " + liberacao.format(FMT_DATA);
    }

    public String situacaoCarenciaClass(RegraCobertura r) {
        if (r == null || !r.isCoberto()) return "inativo";
        if (convenioAtivo == null || convenioAtivo.getDataAdesao() == null) return "pendente";
        int dias = r.getCarenciaDias() != null ? r.getCarenciaDias() : 0;
        LocalDate liberacao = convenioAtivo.getDataAdesao().toLocalDate().plusDays(dias);
        return !LocalDate.now().isBefore(liberacao) ? "ativo" : "pendente";
    }

    public String formatarData(java.time.LocalDateTime dt) {
        return dt != null ? dt.format(FMT_DT) : "—";
    }

    public String formatarDataSimples(LocalDate d) {
        return d != null ? d.format(FMT_DATA) : "—";
    }

    public String descreverTitularidade(TipoTitularidade t) {
        return t != null ? t.getDescricao() : "—";
    }

    private void adicionarMensagem(FacesMessage.Severity severity, String mensagem) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, mensagem, null));
    }

    public PacienteConvenio getConvenioAtivo()           { return convenioAtivo; }
    public List<PacienteConvenio> getHistorico()         { return historico; }
    public List<SolicitacaoConvenio> getSolicitacoes()   { return solicitacoes; }
    public boolean isPossuiPendente()                    { return possuiPendente; }

    public Long getFormConvenioId()                { return formConvenioId; }
    public void setFormConvenioId(Long v)          { this.formConvenioId = v; }
    public Long getFormPlanoId()                   { return formPlanoId; }
    public void setFormPlanoId(Long v)             { this.formPlanoId = v; }
    public String getFormNumeroCarteirinha()       { return formNumeroCarteirinha; }
    public void setFormNumeroCarteirinha(String v) { this.formNumeroCarteirinha = v; }
    public Date getFormDataValidade()              { return formDataValidade; }
    public void setFormDataValidade(Date v)        { this.formDataValidade = v; }
    public String getFormTitularidade()            { return formTitularidade; }
    public void setFormTitularidade(String v)      { this.formTitularidade = v; }
    public String getFormMotivo()                  { return formMotivo; }
    public void setFormMotivo(String v)            { this.formMotivo = v; }
}
