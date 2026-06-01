package br.com.hsg.web.bean.notificacao;

import br.com.hsg.domain.enums.TipoDestinatarioNotificacao;
import br.com.hsg.service.facade.notificacao.NotificacaoServiceFacade;
import br.com.hsg.web.bean.session.BeanSessao;
import br.com.hsg.web.dto.response.UsuarioClinicaDTO;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@RequestScoped
@Named("sinoBean")
public class SinoNotificacoesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject private BeanSessao beanSessao;
    @EJB    private NotificacaoServiceFacade notificacaoService;

    private Long contagemCache;

    public long getContagem() {
        if (contagemCache != null) return contagemCache;
        TipoDestinatarioNotificacao tipo = getTipoDestinatario();
        Long id = getIdDestinatario();
        if (tipo == null || id == null) {
            contagemCache = 0L;
            return 0L;
        }
        contagemCache = notificacaoService.contarNaoLidas(tipo, id);
        return contagemCache;
    }

    public String getContagemRotulo() {
        long n = getContagem();
        if (n <= 0) return "";
        return n > 99 ? "99+" : String.valueOf(n);
    }

    public boolean isTemNaoLidas() {
        return getContagem() > 0;
    }

    public String getLinkNotificacoes() {
        TipoDestinatarioNotificacao tipo = getTipoDestinatario();
        if (tipo == null) return "#";
        switch (tipo) {
            case PACIENTE:   return "/paciente/notificacoes.xhtml";
            case ADMIN:      return "/admin/notificacoes.xhtml";
            case MEDICO:
            case ENFERMEIRO: return "/clinica/notificacoes.xhtml";
            default:         return "#";
        }
    }

    public void refresh() {
        this.contagemCache = null;
    }

    private TipoDestinatarioNotificacao getTipoDestinatario() {
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

    private Long getIdDestinatario() {
        if (beanSessao == null) return null;
        if (beanSessao.getPaciente() != null) return beanSessao.getPaciente().getId();
        if (beanSessao.getAdmin() != null)    return beanSessao.getAdmin().getId();
        UsuarioClinicaDTO u = beanSessao.getUsuarioClinica();
        return u != null ? u.getId() : null;
    }
}
