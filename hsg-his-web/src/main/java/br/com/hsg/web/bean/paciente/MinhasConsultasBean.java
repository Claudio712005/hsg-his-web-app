package br.com.hsg.web.bean.paciente;

import br.com.hsg.domain.entity.Arquivo;
import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.enums.TipoResponsavel;
import br.com.hsg.service.facade.clinica.ReceituarioServiceFacade;
import br.com.hsg.service.facade.paciente.ConsultaServiceFacade;
import br.com.hsg.service.facade.storage.ArquivoServiceFacade;
import br.com.hsg.web.bean.session.BeanSessao;
import org.primefaces.model.UploadedFile;

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
    @EJB    private ArquivoServiceFacade arquivoService;
    @EJB    private ReceituarioServiceFacade receituarioService;

    private List<Consulta> consultas = Collections.emptyList();

    private Long consultaCancelarId;
    private String motivoCancelamento;

    private Consulta consultaSelecionada;

    private Consulta consultaAnexando;
    private List<Arquivo> exames = Collections.emptyList();
    private UploadedFile uploadedExame;

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

    public void abrirAnexarExame(Consulta c) {
        this.consultaAnexando = c;
        try {
            this.exames = arquivoService.listarPorConsulta(c.getId());
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[MinhasConsultasBean] Falha ao listar anexos", ex);
            this.exames = Collections.emptyList();
        }
    }

    public String salvarUploadExame() {
        LOG.info("[MinhasConsultasBean] salvarUploadExame invocado.");
        if (consultaAnexando == null) {
            msg(FacesMessage.SEVERITY_WARN, "Consulta não selecionada.");
            return null;
        }
        if (uploadedExame == null || uploadedExame.getContents() == null
                || uploadedExame.getContents().length == 0) {
            msg(FacesMessage.SEVERITY_WARN, "Selecione um arquivo antes de enviar.");
            return null;
        }
        try {
            arquivoService.anexarExameEmConsulta(consultaAnexando.getId(),
                    uploadedExame.getContents(), uploadedExame.getContentType(),
                    uploadedExame.getFileName(),
                    getPacienteId(), TipoResponsavel.PACIENTE);
            msg(FacesMessage.SEVERITY_INFO, "Exame enviado.");
            this.exames = arquivoService.listarPorConsulta(consultaAnexando.getId());
            this.uploadedExame = null;
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[MinhasConsultasBean] Falha ao anexar exame", ex);
            msg(FacesMessage.SEVERITY_ERROR, extrairMensagem(ex, "Erro ao anexar exame."));
        }
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        return null;
    }

    public void removerExame(Long idArquivo) {
        try {
            arquivoService.remover(idArquivo, getPacienteId(), TipoResponsavel.PACIENTE);
            msg(FacesMessage.SEVERITY_INFO, "Exame removido.");
            if (consultaAnexando != null) {
                this.exames = arquivoService.listarPorConsulta(consultaAnexando.getId());
            }
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[MinhasConsultasBean] Falha ao remover exame", ex);
            msg(FacesMessage.SEVERITY_ERROR, extrairMensagem(ex, "Erro ao remover exame."));
        }
    }

    public boolean temReceita(Consulta c) {
        if (c == null) return false;
        try {
            return receituarioService.buscarPorConsulta(c.getId()) != null;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean podeAnexarExame(Consulta c) {
        if (c == null || c.getStatus() == null) return false;
        return c.getStatus() == br.com.hsg.domain.enums.StatusConsulta.AGENDADA
                || c.getStatus() == br.com.hsg.domain.enums.StatusConsulta.CONFIRMADA;
    }

    public String formatarTamanho(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
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
    public Consulta getConsultaAnexando()           { return consultaAnexando; }
    public List<Arquivo> getExames()                { return exames; }
    public UploadedFile getUploadedExame()          { return uploadedExame; }
    public void setUploadedExame(UploadedFile v)    { this.uploadedExame = v; }
}
