package br.com.hsg.web.bean.clinica;

import br.com.hsg.domain.enums.TipoResponsavel;
import br.com.hsg.service.dto.prontuario.ProntuarioDTO;
import br.com.hsg.service.facade.clinica.ProntuarioServiceFacade;
import br.com.hsg.service.facade.clinica.ProntuarioServiceFacade.PacienteBuscaDTO;
import br.com.hsg.web.bean.session.BeanSessao;
import br.com.hsg.web.dto.response.UsuarioClinicaDTO;

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
@Named("prontuarioBean")
public class ProntuarioBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ProntuarioBean.class.getName());
    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_D  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Inject private BeanSessao beanSessao;
    @EJB    private ProntuarioServiceFacade prontuarioService;

    private Long idPacienteAlvo;
    private PacienteBuscaDTO pacienteSelecionado;
    private ProntuarioDTO prontuario;

    @PostConstruct
    public void init() {
        String paramId = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap().get("idPaciente");
        if (paramId != null && !paramId.isEmpty()) {
            try {
                this.idPacienteAlvo = Long.valueOf(paramId);
                carregar();
            } catch (NumberFormatException nfe) {
                LOG.log(Level.WARNING, "[ProntuarioBean] idPaciente inválido: " + paramId);
            }
        }
    }

    public List<PacienteBuscaDTO> autocompletarPaciente(String termo) {
        try {
            return prontuarioService.buscarPacientes(termo,
                    getResponsavelId(), getTipoResponsavel(), 10);
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[ProntuarioBean] Falha na busca", ex);
            return Collections.emptyList();
        }
    }

    public void abrirPaciente() {
        if (pacienteSelecionado == null) {
            msg(FacesMessage.SEVERITY_WARN, "Selecione um paciente.");
            return;
        }
        this.idPacienteAlvo = pacienteSelecionado.id;
        carregar();
    }

    private void carregar() {
        try {
            this.prontuario = prontuarioService.montarParaPaciente(idPacienteAlvo,
                    getResponsavelId(), getTipoResponsavel());
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[ProntuarioBean] Falha ao carregar prontuário", ex);
            msg(FacesMessage.SEVERITY_ERROR, extrairMensagem(ex, "Erro ao carregar prontuário."));
            this.prontuario = null;
        }
    }

    public String formatarDataHora(LocalDateTime dt) { return dt != null ? dt.format(FMT_DT) : "—"; }
    public String formatarData(java.time.LocalDate d) { return d != null ? d.format(FMT_D) : "—"; }

    public String chipStatusConsulta(String status) {
        if (status == null) return "pendente";
        switch (status) {
            case "AGENDADA":   return "pendente";
            case "CONFIRMADA": return "ativo";
            case "REALIZADA":  return "ativo";
            default:           return "inativo";
        }
    }

    private Long getResponsavelId() {
        if (beanSessao == null) return null;
        if (beanSessao.getAdmin() != null) return beanSessao.getAdmin().getId();
        UsuarioClinicaDTO u = beanSessao.getUsuarioClinica();
        return u != null ? u.getId() : null;
    }

    private TipoResponsavel getTipoResponsavel() {
        if (beanSessao == null) return null;
        if (beanSessao.getAdmin() != null) return TipoResponsavel.ADMIN;
        UsuarioClinicaDTO u = beanSessao.getUsuarioClinica();
        if (u == null || u.getTipo() == null) return null;
        if ("ENFERMEIRO".equalsIgnoreCase(u.getTipo())) return TipoResponsavel.ENFERMEIRO;
        if ("MEDICO".equalsIgnoreCase(u.getTipo()))     return TipoResponsavel.MEDICO;
        return null;
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

    public Long getIdPacienteAlvo()                  { return idPacienteAlvo; }
    public void setIdPacienteAlvo(Long v)            { this.idPacienteAlvo = v; }
    public PacienteBuscaDTO getPacienteSelecionado() { return pacienteSelecionado; }
    public void setPacienteSelecionado(PacienteBuscaDTO v) { this.pacienteSelecionado = v; }
    public ProntuarioDTO getProntuario()             { return prontuario; }
}
