package br.com.hsg.web.bean.paciente;

import br.com.hsg.domain.enums.TipoResponsavel;
import br.com.hsg.service.dto.prontuario.ProntuarioDTO;
import br.com.hsg.service.facade.clinica.ProntuarioServiceFacade;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@ViewScoped
@Named("meuProntuarioBean")
public class MeuProntuarioBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(MeuProntuarioBean.class.getName());
    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_D  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Inject private BeanSessao beanSessao;
    @EJB    private ProntuarioServiceFacade prontuarioService;

    private ProntuarioDTO prontuario;

    @PostConstruct
    public void init() {
        carregar();
    }

    private void carregar() {
        Long idPaciente = getPacienteId();
        if (idPaciente == null) {
            this.prontuario = null;
            return;
        }
        try {
            this.prontuario = prontuarioService.montarParaPaciente(idPaciente, idPaciente,
                    TipoResponsavel.PACIENTE);
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[MeuProntuarioBean] Falha ao carregar prontuário", ex);
            msg(FacesMessage.SEVERITY_ERROR, "Erro ao carregar prontuário.");
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

    private Long getPacienteId() {
        return beanSessao.getPaciente() != null ? beanSessao.getPaciente().getId() : null;
    }

    private void msg(FacesMessage.Severity sev, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(sev, texto, null));
    }

    public ProntuarioDTO getProntuario() { return prontuario; }
}
