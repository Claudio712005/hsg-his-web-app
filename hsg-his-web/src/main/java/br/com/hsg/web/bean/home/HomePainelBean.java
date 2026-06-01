package br.com.hsg.web.bean.home;

import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.entity.Notificacao;
import br.com.hsg.domain.enums.StatusConsulta;
import br.com.hsg.domain.enums.TipoDestinatarioNotificacao;
import br.com.hsg.service.facade.notificacao.NotificacaoServiceFacade;
import br.com.hsg.service.facade.paciente.ConsultaServiceFacade;
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
@Named("homePainelBean")
public class HomePainelBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(HomePainelBean.class.getName());
    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final int LIMITE = 5;

    @Inject private BeanSessao beanSessao;
    @EJB    private ConsultaServiceFacade consultaService;
    @EJB    private NotificacaoServiceFacade notificacaoService;

    private List<Consulta> proximasConsultas = Collections.emptyList();
    private List<Notificacao> ultimasNotificacoes = Collections.emptyList();

    @PostConstruct
    public void init() {
        carregar();
    }

    private void carregar() {
        if (beanSessao.getPaciente() != null) {
            Long id = beanSessao.getPaciente().getId();
            this.proximasConsultas = consultaService.listarProximasPaciente(id, LIMITE);
            this.ultimasNotificacoes = notificacaoService.listar(
                    TipoDestinatarioNotificacao.PACIENTE, id, 0, LIMITE);
        } else if (isMedico()) {
            Long id = beanSessao.getUsuarioClinica().getId();
            this.proximasConsultas = consultaService.listarProximasMedico(id, LIMITE);
            this.ultimasNotificacoes = notificacaoService.listar(
                    TipoDestinatarioNotificacao.MEDICO, id, 0, LIMITE);
        }
    }

    private boolean isMedico() {
        UsuarioClinicaDTO u = beanSessao.getUsuarioClinica();
        return u != null && "MEDICO".equalsIgnoreCase(u.getTipo());
    }

    public void cancelarConsulta(Long idConsulta) {
        try {
            if (beanSessao.getPaciente() == null) {
                msg(FacesMessage.SEVERITY_ERROR, "Apenas pacientes podem cancelar pela tela inicial.");
                return;
            }
            consultaService.cancelarPeloPaciente(idConsulta,
                    beanSessao.getPaciente().getId(),
                    "Cancelamento pelo painel inicial");
            msg(FacesMessage.SEVERITY_INFO, "Consulta cancelada.");
            carregar();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[HomePainelBean] Falha ao cancelar consulta", ex);
            msg(FacesMessage.SEVERITY_ERROR, extrairMensagem(ex, "Erro ao cancelar consulta."));
        }
    }

    public void marcarNotificacaoLida(Long idNotificacao) {
        try {
            TipoDestinatarioNotificacao tipo;
            Long id;
            if (beanSessao.getPaciente() != null) {
                tipo = TipoDestinatarioNotificacao.PACIENTE;
                id = beanSessao.getPaciente().getId();
            } else if (isMedico()) {
                tipo = TipoDestinatarioNotificacao.MEDICO;
                id = beanSessao.getUsuarioClinica().getId();
            } else return;
            notificacaoService.marcarComoLida(idNotificacao, tipo, id);
            carregar();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[HomePainelBean] Falha ao marcar notificação", ex);
        }
    }

    public String formatarDataHora(LocalDateTime dt) { return dt != null ? dt.format(FMT_DT) : "—"; }

    public String chipStatusConsulta(Consulta c) {
        if (c == null || c.getStatus() == null) return "pendente";
        switch (c.getStatus()) {
            case AGENDADA:   return "pendente";
            case CONFIRMADA: return "ativo";
            case REALIZADA:  return "ativo";
            default:         return "inativo";
        }
    }

    public boolean podeCancelar(Consulta c) {
        return c != null
                && c.getDataConsulta() != null
                && c.getDataConsulta().isAfter(LocalDateTime.now().plusHours(24))
                && (c.getStatus() == StatusConsulta.AGENDADA || c.getStatus() == StatusConsulta.CONFIRMADA);
    }

    public List<Consulta> getProximasConsultas() { return proximasConsultas; }
    public List<Notificacao> getUltimasNotificacoes() { return ultimasNotificacoes; }

    public String getLinkVerTodasConsultas() {
        return beanSessao.getPaciente() != null ? "/paciente/minhas-consultas.xhtml" : "#";
    }

    public String getLinkVerTodasNotificacoes() {
        if (beanSessao.getPaciente() != null) return "/paciente/notificacoes.xhtml";
        if (isMedico()) return "/clinica/notificacoes.xhtml";
        return "#";
    }

    public boolean isPaciente() { return beanSessao.getPaciente() != null; }
    public boolean isMedicoLogado() { return isMedico(); }

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
