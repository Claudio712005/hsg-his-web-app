package br.com.hsg.web.bean.clinica;

import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.enums.StatusConsulta;
import br.com.hsg.service.facade.clinica.ConsultaClinicaServiceFacade;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ViewScoped
@Named("minhaAgendaBean")
public class MinhaAgendaBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(MinhaAgendaBean.class.getName());
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FMT_DT   = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    @Inject private BeanSessao beanSessao;
    @EJB    private ConsultaClinicaServiceFacade clinicaService;

    private Date dataInicio;
    private Date dataFim;
    private String statusFiltro;
    private String termoPaciente;
    private List<Consulta> consultas = Collections.emptyList();

    private Long consultaRealizarId;
    private String observacaoClinica;

    private br.com.hsg.domain.entity.Consulta consultaAnotando;
    private String anotacaoTitulo;
    private String anotacaoDescricao;
    private java.util.List<br.com.hsg.domain.entity.ConsultaAnotacao> anotacoes =
            java.util.Collections.emptyList();
    private java.util.List<br.com.hsg.domain.entity.ConsultaHistorico> historicoConsulta =
            java.util.Collections.emptyList();

    @PostConstruct
    public void init() {
        LocalDate hoje = LocalDate.now();
        this.dataInicio = toDate(hoje);
        this.dataFim    = toDate(hoje);
        this.statusFiltro = "TODOS";
        carregar();
    }

    public void aoMudarFiltro() {
        carregar();
    }

    public void aplicarHoje() {
        LocalDate hoje = LocalDate.now();
        this.dataInicio = toDate(hoje);
        this.dataFim    = toDate(hoje);
        carregar();
    }

    public void aplicarProximos7Dias() {
        LocalDate hoje = LocalDate.now();
        this.dataInicio = toDate(hoje);
        this.dataFim    = toDate(hoje.plusDays(7));
        carregar();
    }

    public void limparFiltros() {
        this.statusFiltro  = "TODOS";
        this.termoPaciente = null;
        carregar();
    }

    private void carregar() {
        Long idMedico = getMedicoLogadoId();
        if (idMedico == null || dataInicio == null || dataFim == null) {
            this.consultas = Collections.emptyList();
            return;
        }
        try {
            StatusConsulta status = ("TODOS".equals(statusFiltro) || statusFiltro == null)
                    ? null : StatusConsulta.valueOf(statusFiltro);
            this.consultas = clinicaService.listarConsultasMedicoPorPeriodo(
                    idMedico, toLocalDate(dataInicio), toLocalDate(dataFim),
                    status, termoPaciente);
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[MinhaAgendaBean] Falha ao listar consultas", ex);
            this.consultas = Collections.emptyList();
        }
    }

    public StatusConsulta[] getStatusValues() { return StatusConsulta.values(); }

    public void abrirAnotacoes(br.com.hsg.domain.entity.Consulta c) {
        this.consultaAnotando = c;
        this.anotacaoTitulo = null;
        this.anotacaoDescricao = null;
        try {
            this.anotacoes = clinicaService.listarAnotacoes(c.getId());
            this.historicoConsulta = clinicaService.historicoPorConsulta(c.getId());
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[MinhaAgendaBean] Falha ao listar anotações/histórico", ex);
            this.anotacoes = java.util.Collections.emptyList();
            this.historicoConsulta = java.util.Collections.emptyList();
        }
    }

    public void salvarAnotacao() {
        try {
            if (consultaAnotando == null) {
                msg(FacesMessage.SEVERITY_WARN, "Consulta não selecionada.");
                return;
            }
            clinicaService.adicionarAnotacao(consultaAnotando.getId(),
                    anotacaoTitulo, anotacaoDescricao,
                    getMedicoLogadoId(),
                    br.com.hsg.domain.enums.TipoResponsavel.MEDICO);
            msg(FacesMessage.SEVERITY_INFO, "Anotação registrada.");
            this.anotacaoTitulo = null;
            this.anotacaoDescricao = null;
            this.anotacoes = clinicaService.listarAnotacoes(consultaAnotando.getId());
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[MinhaAgendaBean] Falha ao salvar anotação", ex);
            msg(FacesMessage.SEVERITY_ERROR, extrairMensagem(ex, "Erro ao salvar anotação."));
        }
    }

    public br.com.hsg.domain.entity.Consulta getConsultaAnotando() { return consultaAnotando; }
    public String getAnotacaoTitulo()                              { return anotacaoTitulo; }
    public void setAnotacaoTitulo(String v)                        { this.anotacaoTitulo = v; }
    public String getAnotacaoDescricao()                           { return anotacaoDescricao; }
    public void setAnotacaoDescricao(String v)                     { this.anotacaoDescricao = v; }
    public java.util.List<br.com.hsg.domain.entity.ConsultaAnotacao> getAnotacoes() { return anotacoes; }
    public java.util.List<br.com.hsg.domain.entity.ConsultaHistorico> getHistoricoConsulta() { return historicoConsulta; }

    private Date toDate(LocalDate d) {
        return Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public void prepararRealizar(Long idConsulta) {
        this.consultaRealizarId = idConsulta;
        this.observacaoClinica = null;
    }

    public void marcarRealizada() {
        try {
            if (consultaRealizarId == null) {
                msg(FacesMessage.SEVERITY_WARN, "Selecione uma consulta.");
                return;
            }
            if (observacaoClinica == null || observacaoClinica.trim().isEmpty()) {
                msg(FacesMessage.SEVERITY_WARN, "Informe a observação clínica.");
                return;
            }
            clinicaService.marcarRealizadaComObservacao(consultaRealizarId, getMedicoLogadoId(), observacaoClinica);
            msg(FacesMessage.SEVERITY_INFO, "Consulta marcada como realizada.");
            this.consultaRealizarId = null;
            this.observacaoClinica = null;
            carregar();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[MinhaAgendaBean] Falha ao marcar realizada", ex);
            msg(FacesMessage.SEVERITY_ERROR, extrairMensagem(ex, "Erro ao marcar realizada."));
        }
    }

    public void marcarFalta(Long idConsulta) {
        try {
            clinicaService.marcarFaltaPelaClinica(idConsulta, getMedicoLogadoId(),
                    br.com.hsg.domain.enums.TipoResponsavel.MEDICO);
            msg(FacesMessage.SEVERITY_INFO, "Consulta marcada como falta.");
            carregar();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[MinhaAgendaBean] Falha ao marcar falta", ex);
            msg(FacesMessage.SEVERITY_ERROR, extrairMensagem(ex, "Erro ao marcar falta."));
        }
    }

    public String formatarHora(LocalDateTime dt)     { return dt != null ? dt.format(FMT_HORA) : "—"; }
    public String formatarDataHora(LocalDateTime dt) { return dt != null ? dt.format(FMT_DT) : "—"; }

    public String chipStatus(Consulta c) {
        if (c == null || c.getStatus() == null) return "pendente";
        switch (c.getStatus()) {
            case AGENDADA:   return "pendente";
            case CONFIRMADA: return "ativo";
            case REALIZADA:  return "ativo";
            default:         return "inativo";
        }
    }

    public boolean podeRealizar(Consulta c) {
        return c != null && (c.getStatus() == StatusConsulta.AGENDADA
                || c.getStatus() == StatusConsulta.CONFIRMADA);
    }

    public boolean podeMarcarFalta(Consulta c) { return podeRealizar(c); }

    public int contar(StatusConsulta s) {
        int n = 0;
        for (Consulta c : consultas) if (c.getStatus() == s) n++;
        return n;
    }

    public int getTotalDia()         { return consultas.size(); }
    public int getTotalAgendadas()   { return contar(StatusConsulta.AGENDADA); }
    public int getTotalConfirmadas() { return contar(StatusConsulta.CONFIRMADA); }
    public int getTotalRealizadas()  { return contar(StatusConsulta.REALIZADA); }
    public int getTotalFaltas()      { return contar(StatusConsulta.FALTOU); }

    public Date getDataInicio()                { return dataInicio; }
    public void setDataInicio(Date v)          { this.dataInicio = v; }
    public Date getDataFim()                   { return dataFim; }
    public void setDataFim(Date v)             { this.dataFim = v; }
    public String getStatusFiltro()            { return statusFiltro; }
    public void setStatusFiltro(String v)      { this.statusFiltro = v; }
    public String getTermoPaciente()           { return termoPaciente; }
    public void setTermoPaciente(String v)     { this.termoPaciente = v; }
    public List<Consulta> getConsultas()       { return consultas; }
    public Long getConsultaRealizarId()        { return consultaRealizarId; }
    public String getObservacaoClinica()       { return observacaoClinica; }
    public void setObservacaoClinica(String v) { this.observacaoClinica = v; }

    private LocalDate toLocalDate(Date d) {
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Long getMedicoLogadoId() {
        if (beanSessao.getUsuarioClinica() == null) return null;
        if (!"MEDICO".equalsIgnoreCase(beanSessao.getUsuarioClinica().getTipo())) return null;
        return beanSessao.getUsuarioClinica().getId();
    }

    private void msg(FacesMessage.Severity sev, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(sev, texto, null));
    }

    private String extrairMensagem(Throwable t, String fallback) {
        Throwable cur = t;
        int g = 0;
        while (cur != null && g < 10) {
            if ((cur instanceof IllegalArgumentException || cur instanceof IllegalStateException)
                    && cur.getMessage() != null && !cur.getMessage().isEmpty()) {
                return cur.getMessage();
            }
            cur = cur.getCause();
            g++;
        }
        return (t != null && t.getMessage() != null && !t.getMessage().isEmpty()) ? t.getMessage() : fallback;
    }
}
