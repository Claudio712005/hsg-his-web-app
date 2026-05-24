package br.com.hsg.web.bean.admin;

import br.com.hsg.domain.entity.Convenio;
import br.com.hsg.domain.entity.PlanoConvenio;
import br.com.hsg.domain.entity.RegraCobertura;
import br.com.hsg.domain.enums.TipoCoberturaPlano;
import br.com.hsg.service.facade.admin.ConvenioServiceFacade;
import br.com.hsg.web.bean.session.BeanSessao;
import br.com.hsg.web.model.ConvenioLazyModel;
import br.com.hsg.web.model.PlanoConvenioLazyModel;
import br.com.hsg.web.model.RegraCoberturaLazyModel;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ViewScoped
@Named("convenioBean")
public class ConvenioBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ConvenioBean.class.getName());
    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Inject private BeanSessao beanSessao;
    @EJB    private ConvenioServiceFacade convenioService;

    private ConvenioLazyModel       convenioModel;
    private PlanoConvenioLazyModel  planoModel;
    private RegraCoberturaLazyModel regraModel;

    private String filtroNomeConvenio;
    private String filtroStatusConvenio;

    private Long   filtroConvenioIdPlano;
    private String filtroNomePlano;
    private String filtroCoberturaPlano;
    private String filtroStatusPlano;

    private Long   filtroPlanoIdRegra;
    private String filtroProcedimentoRegra;
    private String filtroCategoriaRegra;
    private String filtroStatusRegra;

    private long totalConveniosAtivos;
    private long totalPlanosAtivos;

    private Convenio       convenioSelecionado;
    private PlanoConvenio  planoSelecionado;
    private RegraCobertura regraSelecionada;

    private Long   formConvenioId;
    private String formConvenioNome;
    private String formConvenioDescricao;
    private String formConvenioRegistroAns;
    private String formConvenioCnpj;
    private String formConvenioSite;
    private String formConvenioTelefone;

    private Long           formPlanoId;
    private Long           formPlanoConvenioId;
    private String         formPlanoNome;
    private String         formPlanoCodigo;
    private String         formPlanoDescricao;
    private String         formPlanoTipoCobertura;
    private BigDecimal     formPlanoValorMensalidade;
    private boolean        formPlanoAcomodacaoIndividual;

    private Long       formRegraId;
    private Long       formRegraPlanoId;
    private String     formRegraProcedimento;
    private String     formRegraCategoria;
    private Integer    formRegraCarenciaDias;
    private BigDecimal formRegraPercentualCopag;
    private boolean    formRegraCobertura;
    private String     formRegraObservacao;

    @PostConstruct
    public void init() {
        convenioModel = new ConvenioLazyModel(convenioService);
        planoModel    = new PlanoConvenioLazyModel(convenioService);
        regraModel    = new RegraCoberturaLazyModel(convenioService);
        atualizarTotais();
        novoConvenio();
        novoPlano();
        novaRegra();
    }

    private void atualizarTotais() {
        this.totalConveniosAtivos = convenioService.contarConveniosAtivos();
        this.totalPlanosAtivos    = convenioService.contarPlanosAtivos();
    }

    public void aplicarFiltrosConvenio() {
        convenioModel.setFiltroNome(filtroNomeConvenio);
        convenioModel.setFiltroStatus(filtroStatusConvenio);
    }

    public void limparFiltrosConvenio() {
        filtroNomeConvenio   = null;
        filtroStatusConvenio = null;
        convenioModel.setFiltroNome(null);
        convenioModel.setFiltroStatus(null);
    }

    public void aplicarFiltrosPlano() {
        planoModel.setFiltroConvenioId(filtroConvenioIdPlano);
        planoModel.setFiltroNome(filtroNomePlano);
        planoModel.setFiltroCobertura(filtroCoberturaPlano);
        planoModel.setFiltroStatus(filtroStatusPlano);
    }

    public void limparFiltrosPlano() {
        filtroConvenioIdPlano = null;
        filtroNomePlano       = null;
        filtroCoberturaPlano  = null;
        filtroStatusPlano     = null;
        planoModel.setFiltroConvenioId(null);
        planoModel.setFiltroNome(null);
        planoModel.setFiltroCobertura(null);
        planoModel.setFiltroStatus(null);
    }

    public void aplicarFiltrosRegra() {
        regraModel.setFiltroPlanoId(filtroPlanoIdRegra);
        regraModel.setFiltroProcedimento(filtroProcedimentoRegra);
        regraModel.setFiltroCategoria(filtroCategoriaRegra);
        regraModel.setFiltroStatus(filtroStatusRegra);
    }

    public void limparFiltrosRegra() {
        filtroPlanoIdRegra      = null;
        filtroProcedimentoRegra = null;
        filtroCategoriaRegra    = null;
        filtroStatusRegra       = null;
        regraModel.setFiltroPlanoId(null);
        regraModel.setFiltroProcedimento(null);
        regraModel.setFiltroCategoria(null);
        regraModel.setFiltroStatus(null);
    }

    public void selecionarConvenio(Convenio c) {
        this.convenioSelecionado = c;
    }

    public void selecionarPlano(PlanoConvenio p) {
        this.planoSelecionado = p;
    }

    public void selecionarRegra(RegraCobertura r) {
        this.regraSelecionada = r;
    }

    public void novoConvenio() {
        this.formConvenioId         = null;
        this.formConvenioNome       = null;
        this.formConvenioDescricao  = null;
        this.formConvenioRegistroAns= null;
        this.formConvenioCnpj       = null;
        this.formConvenioSite       = null;
        this.formConvenioTelefone   = null;
    }

    public void prepararEdicaoConvenio(Convenio c) {
        this.formConvenioId          = c.getId();
        this.formConvenioNome        = c.getNome();
        this.formConvenioDescricao   = c.getDescricao();
        this.formConvenioRegistroAns = c.getRegistroAns();
        this.formConvenioCnpj        = c.getCnpj();
        this.formConvenioSite        = c.getSite();
        this.formConvenioTelefone    = c.getTelefone();
    }

    public void salvarConvenio() {
        try {
            if (formConvenioId == null) {
                convenioService.criarConvenio(formConvenioNome, formConvenioDescricao,
                        formConvenioRegistroAns, formConvenioCnpj, formConvenioSite, formConvenioTelefone);
                adicionarMensagem(FacesMessage.SEVERITY_INFO, "Convênio cadastrado com sucesso.");
            } else {
                convenioService.atualizarConvenio(formConvenioId, formConvenioNome, formConvenioDescricao,
                        formConvenioRegistroAns, formConvenioCnpj, formConvenioSite, formConvenioTelefone);
                adicionarMensagem(FacesMessage.SEVERITY_INFO, "Convênio atualizado com sucesso.");
            }
            atualizarTotais();
            novoConvenio();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "[ConvenioBean] Erro ao salvar convênio", ex);
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro inesperado ao salvar convênio.");
        }
    }

    public void inativarConvenio(Long id) {
        try {
            convenioService.inativarConvenio(id);
            atualizarTotais();
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Convênio inativado.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        }
    }

    public void ativarConvenio(Long id) {
        try {
            convenioService.ativarConvenio(id);
            atualizarTotais();
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Convênio ativado.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        }
    }

    public void novoPlano() {
        this.formPlanoId                   = null;
        this.formPlanoConvenioId           = null;
        this.formPlanoNome                 = null;
        this.formPlanoCodigo               = null;
        this.formPlanoDescricao            = null;
        this.formPlanoTipoCobertura        = null;
        this.formPlanoValorMensalidade     = null;
        this.formPlanoAcomodacaoIndividual = false;
    }

    public void prepararEdicaoPlano(PlanoConvenio p) {
        this.formPlanoId                   = p.getId();
        this.formPlanoConvenioId           = p.getConvenio().getId();
        this.formPlanoNome                 = p.getNome();
        this.formPlanoCodigo               = p.getCodigo();
        this.formPlanoDescricao            = p.getDescricao();
        this.formPlanoTipoCobertura        = p.getTipoCobertura() != null ? p.getTipoCobertura().getValor() : null;
        this.formPlanoValorMensalidade     = p.getValorMensalidade();
        this.formPlanoAcomodacaoIndividual = p.isAcomodacaoIndividual();
    }

    public void salvarPlano() {
        try {
            TipoCoberturaPlano tipo = formPlanoTipoCobertura != null
                    ? TipoCoberturaPlano.fromValor(formPlanoTipoCobertura) : null;
            if (formPlanoId == null) {
                convenioService.criarPlano(formPlanoConvenioId, formPlanoNome, formPlanoCodigo,
                        formPlanoDescricao, tipo, formPlanoValorMensalidade, formPlanoAcomodacaoIndividual);
                adicionarMensagem(FacesMessage.SEVERITY_INFO, "Plano cadastrado com sucesso.");
            } else {
                convenioService.atualizarPlano(formPlanoId, formPlanoNome, formPlanoCodigo,
                        formPlanoDescricao, tipo, formPlanoValorMensalidade, formPlanoAcomodacaoIndividual);
                adicionarMensagem(FacesMessage.SEVERITY_INFO, "Plano atualizado com sucesso.");
            }
            atualizarTotais();
            novoPlano();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "[ConvenioBean] Erro ao salvar plano", ex);
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro inesperado ao salvar plano.");
        }
    }

    public void inativarPlano(Long id) {
        try {
            convenioService.inativarPlano(id);
            atualizarTotais();
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Plano inativado.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        }
    }

    public void ativarPlano(Long id) {
        try {
            convenioService.ativarPlano(id);
            atualizarTotais();
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Plano ativado.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        }
    }

    public void novaRegra() {
        this.formRegraId             = null;
        this.formRegraPlanoId        = null;
        this.formRegraProcedimento   = null;
        this.formRegraCategoria      = null;
        this.formRegraCarenciaDias   = 0;
        this.formRegraPercentualCopag = BigDecimal.ZERO;
        this.formRegraCobertura      = true;
        this.formRegraObservacao     = null;
    }

    public void prepararEdicaoRegra(RegraCobertura r) {
        this.formRegraId              = r.getId();
        this.formRegraPlanoId         = r.getPlano().getId();
        this.formRegraProcedimento    = r.getProcedimento();
        this.formRegraCategoria       = r.getCategoria();
        this.formRegraCarenciaDias    = r.getCarenciaDias();
        this.formRegraPercentualCopag = r.getPercentualCopagamento();
        this.formRegraCobertura       = r.isCoberto();
        this.formRegraObservacao      = r.getObservacao();
    }

    public void salvarRegra() {
        try {
            if (formRegraId == null) {
                convenioService.criarRegra(formRegraPlanoId, formRegraProcedimento, formRegraCategoria,
                        formRegraCarenciaDias, formRegraPercentualCopag, formRegraCobertura,
                        formRegraObservacao);
                adicionarMensagem(FacesMessage.SEVERITY_INFO, "Regra cadastrada com sucesso.");
            } else {
                convenioService.atualizarRegra(formRegraId, formRegraProcedimento, formRegraCategoria,
                        formRegraCarenciaDias, formRegraPercentualCopag, formRegraCobertura,
                        formRegraObservacao);
                adicionarMensagem(FacesMessage.SEVERITY_INFO, "Regra atualizada com sucesso.");
            }
            novaRegra();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "[ConvenioBean] Erro ao salvar regra", ex);
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro inesperado ao salvar regra.");
        }
    }

    public void inativarRegra(Long id) {
        try {
            convenioService.inativarRegra(id);
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Regra inativada.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        }
    }

    public void ativarRegra(Long id) {
        try {
            convenioService.ativarRegra(id);
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Regra ativada.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        }
    }

    public List<Convenio> getConveniosParaSelect() {
        return convenioService.listarConveniosAtivos();
    }

    public List<PlanoConvenio> getPlanosAtivosTodos() {
        List<Convenio> conv = convenioService.listarConveniosAtivos();
        java.util.List<PlanoConvenio> todos = new java.util.ArrayList<>();
        for (Convenio c : conv) {
            todos.addAll(convenioService.listarPlanosAtivosPorConvenio(c.getId()));
        }
        return todos;
    }

    public String descreverPlano(PlanoConvenio p) {
        if (p == null) return "—";
        return p.getConvenio().getNome() + " — " + p.getNome();
    }

    public TipoCoberturaPlano[] getTiposCobertura() {
        return TipoCoberturaPlano.values();
    }

    public String formatarTipoCobertura(TipoCoberturaPlano t) {
        return t != null ? t.getDescricao() : "—";
    }

    public String formatarData(LocalDateTime dt) {
        return dt != null ? dt.format(FMT_DT) : "—";
    }

    public String formatarBoolean(boolean v) {
        return v ? "Sim" : "Não";
    }

    public void sair() throws IOException {
        beanSessao.encerrarSessao();
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.invalidateSession();
        ec.redirect(ec.getRequestContextPath() + "/logout");
    }

    private void adicionarMensagem(FacesMessage.Severity severity, String mensagem) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, mensagem, null));
    }

    public ConvenioLazyModel getConvenioModel()  { return convenioModel; }
    public PlanoConvenioLazyModel getPlanoModel(){ return planoModel; }
    public RegraCoberturaLazyModel getRegraModel(){ return regraModel; }

    public String getFiltroNomeConvenio()             { return filtroNomeConvenio; }
    public void setFiltroNomeConvenio(String v)       { this.filtroNomeConvenio = v; }
    public String getFiltroStatusConvenio()           { return filtroStatusConvenio; }
    public void setFiltroStatusConvenio(String v)     { this.filtroStatusConvenio = v; }

    public Long getFiltroConvenioIdPlano()            { return filtroConvenioIdPlano; }
    public void setFiltroConvenioIdPlano(Long v)      { this.filtroConvenioIdPlano = v; }
    public String getFiltroNomePlano()                { return filtroNomePlano; }
    public void setFiltroNomePlano(String v)          { this.filtroNomePlano = v; }
    public String getFiltroCoberturaPlano()           { return filtroCoberturaPlano; }
    public void setFiltroCoberturaPlano(String v)     { this.filtroCoberturaPlano = v; }
    public String getFiltroStatusPlano()              { return filtroStatusPlano; }
    public void setFiltroStatusPlano(String v)        { this.filtroStatusPlano = v; }

    public Long getFiltroPlanoIdRegra()               { return filtroPlanoIdRegra; }
    public void setFiltroPlanoIdRegra(Long v)         { this.filtroPlanoIdRegra = v; }
    public String getFiltroProcedimentoRegra()        { return filtroProcedimentoRegra; }
    public void setFiltroProcedimentoRegra(String v)  { this.filtroProcedimentoRegra = v; }
    public String getFiltroCategoriaRegra()           { return filtroCategoriaRegra; }
    public void setFiltroCategoriaRegra(String v)     { this.filtroCategoriaRegra = v; }
    public String getFiltroStatusRegra()              { return filtroStatusRegra; }
    public void setFiltroStatusRegra(String v)        { this.filtroStatusRegra = v; }

    public long getTotalConveniosAtivos() { return totalConveniosAtivos; }
    public long getTotalPlanosAtivos()    { return totalPlanosAtivos; }

    public Convenio getConvenioSelecionado()           { return convenioSelecionado; }
    public void setConvenioSelecionado(Convenio c)     { this.convenioSelecionado = c; }
    public PlanoConvenio getPlanoSelecionado()         { return planoSelecionado; }
    public void setPlanoSelecionado(PlanoConvenio p)   { this.planoSelecionado = p; }
    public RegraCobertura getRegraSelecionada()        { return regraSelecionada; }
    public void setRegraSelecionada(RegraCobertura r)  { this.regraSelecionada = r; }

    public Long getFormConvenioId()                    { return formConvenioId; }
    public void setFormConvenioId(Long v)              { this.formConvenioId = v; }
    public String getFormConvenioNome()                { return formConvenioNome; }
    public void setFormConvenioNome(String v)          { this.formConvenioNome = v; }
    public String getFormConvenioDescricao()           { return formConvenioDescricao; }
    public void setFormConvenioDescricao(String v)     { this.formConvenioDescricao = v; }
    public String getFormConvenioRegistroAns()         { return formConvenioRegistroAns; }
    public void setFormConvenioRegistroAns(String v)   { this.formConvenioRegistroAns = v; }
    public String getFormConvenioCnpj()                { return formConvenioCnpj; }
    public void setFormConvenioCnpj(String v)          { this.formConvenioCnpj = v; }
    public String getFormConvenioSite()                { return formConvenioSite; }
    public void setFormConvenioSite(String v)          { this.formConvenioSite = v; }
    public String getFormConvenioTelefone()            { return formConvenioTelefone; }
    public void setFormConvenioTelefone(String v)      { this.formConvenioTelefone = v; }

    public Long getFormPlanoId()                       { return formPlanoId; }
    public void setFormPlanoId(Long v)                 { this.formPlanoId = v; }
    public Long getFormPlanoConvenioId()               { return formPlanoConvenioId; }
    public void setFormPlanoConvenioId(Long v)         { this.formPlanoConvenioId = v; }
    public String getFormPlanoNome()                   { return formPlanoNome; }
    public void setFormPlanoNome(String v)             { this.formPlanoNome = v; }
    public String getFormPlanoCodigo()                 { return formPlanoCodigo; }
    public void setFormPlanoCodigo(String v)           { this.formPlanoCodigo = v; }
    public String getFormPlanoDescricao()              { return formPlanoDescricao; }
    public void setFormPlanoDescricao(String v)        { this.formPlanoDescricao = v; }
    public String getFormPlanoTipoCobertura()          { return formPlanoTipoCobertura; }
    public void setFormPlanoTipoCobertura(String v)    { this.formPlanoTipoCobertura = v; }
    public BigDecimal getFormPlanoValorMensalidade()   { return formPlanoValorMensalidade; }
    public void setFormPlanoValorMensalidade(BigDecimal v) { this.formPlanoValorMensalidade = v; }
    public boolean isFormPlanoAcomodacaoIndividual()   { return formPlanoAcomodacaoIndividual; }
    public void setFormPlanoAcomodacaoIndividual(boolean v) { this.formPlanoAcomodacaoIndividual = v; }

    public Long getFormRegraId()                       { return formRegraId; }
    public void setFormRegraId(Long v)                 { this.formRegraId = v; }
    public Long getFormRegraPlanoId()                  { return formRegraPlanoId; }
    public void setFormRegraPlanoId(Long v)            { this.formRegraPlanoId = v; }
    public String getFormRegraProcedimento()           { return formRegraProcedimento; }
    public void setFormRegraProcedimento(String v)     { this.formRegraProcedimento = v; }
    public String getFormRegraCategoria()              { return formRegraCategoria; }
    public void setFormRegraCategoria(String v)        { this.formRegraCategoria = v; }
    public Integer getFormRegraCarenciaDias()          { return formRegraCarenciaDias; }
    public void setFormRegraCarenciaDias(Integer v)    { this.formRegraCarenciaDias = v; }
    public BigDecimal getFormRegraPercentualCopag()    { return formRegraPercentualCopag; }
    public void setFormRegraPercentualCopag(BigDecimal v) { this.formRegraPercentualCopag = v; }
    public boolean isFormRegraCobertura()              { return formRegraCobertura; }
    public void setFormRegraCobertura(boolean v)       { this.formRegraCobertura = v; }
    public String getFormRegraObservacao()             { return formRegraObservacao; }
    public void setFormRegraObservacao(String v)       { this.formRegraObservacao = v; }
}
