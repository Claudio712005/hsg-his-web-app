package br.com.hsg.web.bean.paciente;

import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.service.facade.paciente.ConsultaServiceFacade;
import br.com.hsg.web.bean.session.BeanSessao;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ViewScoped
@Named("minhasConsultasBean")
public class MinhasConsultasBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(MinhasConsultasBean.class.getName());
    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Inject private BeanSessao beanSessao;
    @EJB    private ConsultaServiceFacade consultaService;

    private List<Consulta> consultas = Collections.emptyList();

    private Long consultaCancelarId;
    private String motivoCancelamento;

    private Consulta consultaSelecionada;

    @PostConstruct
    public void init() {
        carregar();
    }

    private void carregar() {
        if (getPacienteId() == null) {
            consultas = Collections.emptyList();
            return;
        }
        this.consultas = consultaService.listarConsultasPaciente(getPacienteId());
    }

    public void verDetalhes(Consulta c) {
        this.consultaSelecionada = c;
    }

    public void prepararCancelamento(Long idConsulta) {
        this.consultaCancelarId = idConsulta;
        this.motivoCancelamento = null;
    }

    public void cancelar() {
        try {
            if (consultaCancelarId == null) {
                msg(FacesMessage.SEVERITY_WARN, "Selecione uma consulta.");
                return;
            }
            if (motivoCancelamento == null || motivoCancelamento.trim().isEmpty()) {
                msg(FacesMessage.SEVERITY_WARN, "Informe o motivo do cancelamento.");
                return;
            }
            consultaService.cancelarPeloPaciente(consultaCancelarId, getPacienteId(), motivoCancelamento);
            msg(FacesMessage.SEVERITY_INFO, "Consulta cancelada.");
            this.consultaCancelarId = null;
            this.motivoCancelamento = null;
            carregar();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[MinhasConsultasBean] Falha ao cancelar", ex);
            msg(FacesMessage.SEVERITY_ERROR, extrairMensagem(ex, "Erro ao cancelar consulta."));
        }
    }

    public String formatarDataHora(LocalDateTime dt) { return dt != null ? dt.format(FMT_DT) : "—"; }

    public String chipStatus(Consulta c) {
        switch (c.getStatus()) {
            case AGENDADA:   return "pendente";
            case CONFIRMADA: return "ativo";
            case REALIZADA:  return "ativo";
            case CANCELADA:  return "inativo";
            case FALTOU:     return "inativo";
            default:         return "pendente";
        }
    }

    private Long getPacienteId() {
        return beanSessao.getPaciente() != null ? beanSessao.getPaciente().getId() : null;
    }

    private void msg(FacesMessage.Severity sev, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(sev, texto, null));
    }

    private String extrairMensagem(Throwable t, String fallback) {
        Throwable cur = t;
        int guard = 0;
        while (cur != null && guard < 10) {
            if ((cur instanceof IllegalArgumentException || cur instanceof IllegalStateException)
                    && cur.getMessage() != null && !cur.getMessage().isEmpty()) {
                return cur.getMessage();
            }
            cur = cur.getCause();
            guard++;
        }
        return (t != null && t.getMessage() != null && !t.getMessage().isEmpty()) ? t.getMessage() : fallback;
    }

    public List<Consulta> getConsultas()            { return consultas; }
    public Long getConsultaCancelarId()             { return consultaCancelarId; }
    public void setConsultaCancelarId(Long v)       { this.consultaCancelarId = v; }
    public String getMotivoCancelamento()           { return motivoCancelamento; }
    public void setMotivoCancelamento(String v)     { this.motivoCancelamento = v; }
    public Consulta getConsultaSelecionada()        { return consultaSelecionada; }
}
