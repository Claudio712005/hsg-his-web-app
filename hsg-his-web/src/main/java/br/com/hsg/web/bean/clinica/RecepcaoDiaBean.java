package br.com.hsg.web.bean.clinica;

import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.entity.Especialidade;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.enums.StatusConsulta;
import br.com.hsg.domain.enums.TipoResponsavel;
import br.com.hsg.web.dto.response.UsuarioClinicaDTO;
import br.com.hsg.domain.entity.Arquivo;
import br.com.hsg.service.facade.admin.AgendaMedicaServiceFacade;
import br.com.hsg.service.facade.clinica.ConsultaClinicaServiceFacade;
import br.com.hsg.service.facade.paciente.ConsultaBuscaServiceFacade;
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
@Named("recepcaoDiaBean")
public class RecepcaoDiaBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(RecepcaoDiaBean.class.getName());
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FMT_DT   = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    @Inject private BeanSessao beanSessao;
    @EJB    private ConsultaClinicaServiceFacade clinicaService;
    @EJB    private AgendaMedicaServiceFacade agendaService;
    @EJB    private ConsultaBuscaServiceFacade buscaService;
    @EJB    private ArquivoServiceFacade arquivoService;

    private Date dataInicio;
    private Date dataFim;
    private Long medicoFiltroId;
    private Long especialidadeFiltroId;
    private String statusFiltro;
    private String termoPaciente;
    private List<Medico> medicos = Collections.emptyList();
    private List<Especialidade> especialidades = Collections.emptyList();
    private List<Consulta> consultas = Collections.emptyList();

    private Long consultaCancelarId;
    private String motivoCancelamento;

    private br.com.hsg.domain.entity.Consulta consultaAnotando;
    private String anotacaoTitulo;
    private String anotacaoDescricao;
    private java.util.List<br.com.hsg.domain.entity.ConsultaAnotacao> anotacoes =
            java.util.Collections.emptyList();
    private java.util.List<br.com.hsg.domain.entity.ConsultaHistorico> historicoConsulta =
            java.util.Collections.emptyList();
    private java.util.List<Arquivo> anexosConsulta = java.util.Collections.emptyList();
    private UploadedFile uploadedAnexo;

    @PostConstruct
    public void init() {
        LocalDate hoje = LocalDate.now();
        this.dataInicio = toDate(hoje);
        this.dataFim    = toDate(hoje);
        this.statusFiltro = "TODOS";
        this.medicos = agendaService.listarMedicosAtivos();
        try {
            this.especialidades = buscaService.listarEspecialidadesAtivas();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[RecepcaoDiaBean] Falha ao listar especialidades", ex);
        }
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
        this.medicoFiltroId       = null;
        this.especialidadeFiltroId = null;
        this.statusFiltro         = "TODOS";
        this.termoPaciente        = null;
        carregar();
    }

    private void carregar() {
        if (dataInicio == null || dataFim == null) {
            this.consultas = Collections.emptyList();
            return;
        }
        try {
            StatusConsulta status = ("TODOS".equals(statusFiltro) || statusFiltro == null)
                    ? null : StatusConsulta.valueOf(statusFiltro);
            this.consultas = clinicaService.listarConsultasPorPeriodo(
                    toLocalDate(dataInicio), toLocalDate(dataFim),
                    medicoFiltroId, status, termoPaciente, especialidadeFiltroId);
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[RecepcaoDiaBean] Falha ao listar consultas", ex);
            this.consultas = Collections.emptyList();
        }
    }

    public StatusConsulta[] getStatusValues() { return StatusConsulta.values(); }

    private Date toDate(LocalDate d) {
        return Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public void confirmarChegada(Long idConsulta) {
        try {
            clinicaService.confirmarChegada(idConsulta, getResponsavelId(), getTipoResponsavel());
            msg(FacesMessage.SEVERITY_INFO, "Chegada confirmada.");
            carregar();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[RecepcaoDiaBean] Falha ao confirmar chegada", ex);
            msg(FacesMessage.SEVERITY_ERROR, extrairMensagem(ex, "Erro ao confirmar chegada."));
        }
    }

    public void marcarFalta(Long idConsulta) {
        try {
            clinicaService.marcarFaltaPelaClinica(idConsulta, getResponsavelId(), getTipoResponsavel());
            msg(FacesMessage.SEVERITY_INFO, "Consulta marcada como falta.");
            carregar();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[RecepcaoDiaBean] Falha ao marcar falta", ex);
            msg(FacesMessage.SEVERITY_ERROR, extrairMensagem(ex, "Erro ao marcar falta."));
        }
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
            clinicaService.cancelarPelaClinica(consultaCancelarId, getResponsavelId(),
                    getTipoResponsavel(), motivoCancelamento);
            msg(FacesMessage.SEVERITY_INFO, "Consulta cancelada.");
            this.consultaCancelarId = null;
            this.motivoCancelamento = null;
            carregar();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[RecepcaoDiaBean] Falha ao cancelar", ex);
            msg(FacesMessage.SEVERITY_ERROR, extrairMensagem(ex, "Erro ao cancelar consulta."));
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

    public boolean podeConfirmar(Consulta c) {
        return c != null && c.getStatus() == StatusConsulta.AGENDADA;
    }

    public boolean podeMarcarFalta(Consulta c) {
        return c != null && (c.getStatus() == StatusConsulta.AGENDADA
                || c.getStatus() == StatusConsulta.CONFIRMADA);
    }

    public boolean podeCancelar(Consulta c) {
        return podeMarcarFalta(c);
    }

    public int contar(StatusConsulta status) {
        int n = 0;
        for (Consulta c : consultas) if (c.getStatus() == status) n++;
        return n;
    }

    public int getTotalDia()         { return consultas.size(); }
    public int getTotalAgendadas()   { return contar(StatusConsulta.AGENDADA); }
    public int getTotalConfirmadas() { return contar(StatusConsulta.CONFIRMADA); }
    public int getTotalRealizadas()  { return contar(StatusConsulta.REALIZADA); }
    public int getTotalFaltas()      { return contar(StatusConsulta.FALTOU); }
    public int getTotalCanceladas()  { return contar(StatusConsulta.CANCELADA); }

    public Date getDataInicio()                { return dataInicio; }
    public void setDataInicio(Date v)          { this.dataInicio = v; }
    public Date getDataFim()                   { return dataFim; }
    public void setDataFim(Date v)             { this.dataFim = v; }
    public Long getMedicoFiltroId()            { return medicoFiltroId; }
    public void setMedicoFiltroId(Long v)      { this.medicoFiltroId = v; }
    public Long getEspecialidadeFiltroId()     { return especialidadeFiltroId; }
    public void setEspecialidadeFiltroId(Long v){ this.especialidadeFiltroId = v; }
    public String getStatusFiltro()            { return statusFiltro; }
    public void setStatusFiltro(String v)      { this.statusFiltro = v; }
    public String getTermoPaciente()           { return termoPaciente; }
    public void setTermoPaciente(String v)     { this.termoPaciente = v; }
    public List<Medico> getMedicos()           { return medicos; }
    public List<Especialidade> getEspecialidades() { return especialidades; }
    public List<Consulta> getConsultas()       { return consultas; }
    public Long getConsultaCancelarId()        { return consultaCancelarId; }
    public String getMotivoCancelamento()      { return motivoCancelamento; }
    public void setMotivoCancelamento(String v){ this.motivoCancelamento = v; }

    private LocalDate toLocalDate(Date d) {
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Long getResponsavelId() {
        if (beanSessao.getAdmin() != null) return beanSessao.getAdmin().getId();
        if (beanSessao.getUsuarioClinica() != null) return beanSessao.getUsuarioClinica().getId();
        return null;
    }

    public TipoResponsavel getTipoResponsavel() {
        if (beanSessao.getAdmin() != null) return TipoResponsavel.ADMIN;
        UsuarioClinicaDTO u = beanSessao.getUsuarioClinica();
        if (u != null && "ENFERMEIRO".equalsIgnoreCase(u.getTipo())) return TipoResponsavel.ENFERMEIRO;
        if (u != null && "MEDICO".equalsIgnoreCase(u.getTipo()))    return TipoResponsavel.MEDICO;
        return null;
    }

    public void abrirAnotacoes(br.com.hsg.domain.entity.Consulta c) {
        this.consultaAnotando = c;
        this.anotacaoTitulo = null;
        this.anotacaoDescricao = null;
        try {
            this.anotacoes = clinicaService.listarAnotacoes(c.getId());
            this.historicoConsulta = clinicaService.historicoPorConsulta(c.getId());
            this.anexosConsulta = arquivoService.listarPorConsulta(c.getId());
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[RecepcaoDiaBean] Falha ao listar anotações/histórico", ex);
            this.anotacoes = java.util.Collections.emptyList();
            this.historicoConsulta = java.util.Collections.emptyList();
            this.anexosConsulta = java.util.Collections.emptyList();
        }
    }

    public String salvarUploadAnexo() {
        if (consultaAnotando == null) {
            msg(FacesMessage.SEVERITY_WARN, "Consulta não selecionada.");
            return null;
        }
        if (uploadedAnexo == null || uploadedAnexo.getContents() == null
                || uploadedAnexo.getContents().length == 0) {
            msg(FacesMessage.SEVERITY_WARN, "Selecione um arquivo antes de enviar.");
            return null;
        }
        try {
            arquivoService.anexarEmConsulta(consultaAnotando.getId(),
                    uploadedAnexo.getContents(), uploadedAnexo.getContentType(),
                    uploadedAnexo.getFileName(),
                    getResponsavelId(), getTipoResponsavel());
            msg(FacesMessage.SEVERITY_INFO, "Anexo enviado.");
            this.anexosConsulta = arquivoService.listarPorConsulta(consultaAnotando.getId());
            this.uploadedAnexo = null;
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[RecepcaoDiaBean] Falha ao anexar arquivo", ex);
            msg(FacesMessage.SEVERITY_ERROR, extrairMensagem(ex, "Erro ao anexar arquivo."));
        }
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        return null;
    }

    public void removerAnexo(Long idArquivo) {
        try {
            arquivoService.remover(idArquivo, getResponsavelId(), getTipoResponsavel());
            msg(FacesMessage.SEVERITY_INFO, "Anexo removido.");
            if (consultaAnotando != null) {
                this.anexosConsulta = arquivoService.listarPorConsulta(consultaAnotando.getId());
            }
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[RecepcaoDiaBean] Falha ao remover anexo", ex);
            msg(FacesMessage.SEVERITY_ERROR, extrairMensagem(ex, "Erro ao remover anexo."));
        }
    }

    public String formatarTamanho(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    public void salvarAnotacao() {
        try {
            if (consultaAnotando == null) {
                msg(FacesMessage.SEVERITY_WARN, "Consulta não selecionada.");
                return;
            }
            clinicaService.adicionarAnotacao(consultaAnotando.getId(),
                    anotacaoTitulo, anotacaoDescricao,
                    getResponsavelId(), getTipoResponsavel());
            msg(FacesMessage.SEVERITY_INFO, "Anotação registrada.");
            this.anotacaoTitulo = null;
            this.anotacaoDescricao = null;
            this.anotacoes = clinicaService.listarAnotacoes(consultaAnotando.getId());
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[RecepcaoDiaBean] Falha ao salvar anotação", ex);
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
    public java.util.List<Arquivo> getAnexosConsulta() { return anexosConsulta; }
    public UploadedFile getUploadedAnexo()           { return uploadedAnexo; }
    public void setUploadedAnexo(UploadedFile v)     { this.uploadedAnexo = v; }

    public boolean isAcessoPermitido() {
        TipoResponsavel t = getTipoResponsavel();
        return t == TipoResponsavel.ADMIN || t == TipoResponsavel.ENFERMEIRO;
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
