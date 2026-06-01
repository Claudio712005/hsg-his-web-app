package br.com.hsg.web.bean.notificacao;

import br.com.hsg.domain.entity.Notificacao;
import br.com.hsg.domain.enums.TipoDestinatarioNotificacao;
import br.com.hsg.service.facade.notificacao.NotificacaoServiceFacade;
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
@Named("notificacoesBean")
public class NotificacoesBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(NotificacoesBean.class.getName());
    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final int LIMITE_LISTAGEM = 100;

    @Inject private BeanSessao beanSessao;
    @EJB    private NotificacaoServiceFacade notificacaoService;

    private List<Notificacao> notificacoes = Collections.emptyList();
    private long naoLidas;

    private String filtroLida = "TODAS";
    private String termoBusca;

    @PostConstruct
    public void init() {
        carregar();
    }

    private void carregar() {
        TipoDestinatarioNotificacao tipo = getTipoDestinatario();
        Long id = getIdDestinatario();
        if (tipo == null || id == null) {
            this.notificacoes = Collections.emptyList();
            this.naoLidas    = 0L;
            return;
        }
        Boolean lidaFlag = null;
        if ("LIDAS".equals(filtroLida))      lidaFlag = Boolean.TRUE;
        if ("NAO_LIDAS".equals(filtroLida))  lidaFlag = Boolean.FALSE;

        this.notificacoes = notificacaoService.listarFiltrado(tipo, id, lidaFlag, termoBusca,
                0, LIMITE_LISTAGEM);
        this.naoLidas    = notificacaoService.contarNaoLidas(tipo, id);
    }

    public void aplicarFiltro() {
        carregar();
    }

    public void limparFiltro() {
        this.filtroLida  = "TODAS";
        this.termoBusca  = null;
        carregar();
    }

    public String abrirEMarcar(Notificacao n) {
        try {
            if (!n.isLida()) {
                notificacaoService.marcarComoLida(n.getId(), getTipoDestinatario(), getIdDestinatario());
            }
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[NotificacoesBean] Falha ao marcar como lida em abertura", ex);
        }
        try {
            javax.faces.context.ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            String destino = ec.getRequestContextPath() + (n.getLink() != null ? n.getLink() : "/");
            ec.redirect(destino);
        } catch (java.io.IOException io) {
            LOG.log(Level.WARNING, "[NotificacoesBean] Falha ao redirecionar para link", io);
        }
        return null;
    }

    public void marcarComoLida(Notificacao n) {
        try {
            notificacaoService.marcarComoLida(n.getId(), getTipoDestinatario(), getIdDestinatario());
            carregar();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[NotificacoesBean] Falha ao marcar como lida", ex);
            msg(FacesMessage.SEVERITY_ERROR, "Erro ao marcar como lida.");
        }
    }

    public void marcarTodasComoLidas() {
        try {
            notificacaoService.marcarTodasComoLidas(getTipoDestinatario(), getIdDestinatario());
            carregar();
            msg(FacesMessage.SEVERITY_INFO, "Todas as notificações foram marcadas como lidas.");
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[NotificacoesBean] Falha ao marcar todas", ex);
            msg(FacesMessage.SEVERITY_ERROR, "Erro ao marcar todas como lidas.");
        }
    }

    public String formatarDataHora(LocalDateTime dt) { return dt != null ? dt.format(FMT_DT) : "—"; }

    public String chipTipo(Notificacao n) {
        if (n == null || n.getTipo() == null) return "pendente";
        switch (n.getTipo()) {
            case SUCESSO: return "ativo";
            case ALERTA:  return "pendente";
            case ERRO:    return "inativo";
            default:      return "pendente";
        }
    }

    public TipoDestinatarioNotificacao getTipoDestinatario() {
        if (beanSessao == null) return null;
        if (beanSessao.getPaciente() != null) return TipoDestinatarioNotificacao.PACIENTE;
        if (beanSessao.getAdmin() != null)    return TipoDestinatarioNotificacao.ADMIN;
        UsuarioClinicaDTO u = beanSessao.getUsuarioClinica();
        if (u != null && u.getTipo() != null) {
            if ("ENFERMEIRO".equalsIgnoreCase(u.getTipo())) return TipoDestinatarioNotificacao.ENFERMEIRO;
            return TipoDestinatarioNotificacao.MEDICO;
        }
        return null;
    }

    public Long getIdDestinatario() {
        if (beanSessao == null) return null;
        if (beanSessao.getPaciente() != null) return beanSessao.getPaciente().getId();
        if (beanSessao.getAdmin() != null)    return beanSessao.getAdmin().getId();
        UsuarioClinicaDTO u = beanSessao.getUsuarioClinica();
        return u != null ? u.getId() : null;
    }

    public List<Notificacao> getNotificacoes() { return notificacoes; }
    public long getNaoLidas()                  { return naoLidas; }
    public String getFiltroLida()              { return filtroLida; }
    public void setFiltroLida(String v)        { this.filtroLida = v; }
    public String getTermoBusca()              { return termoBusca; }
    public void setTermoBusca(String v)        { this.termoBusca = v; }

    private void msg(FacesMessage.Severity sev, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(sev, texto, null));
    }
}
