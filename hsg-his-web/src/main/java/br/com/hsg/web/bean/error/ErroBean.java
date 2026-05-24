package br.com.hsg.web.bean.error;

import br.com.hsg.web.bean.session.BeanSessao;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@ViewScoped
@Named("erroBean")
public class ErroBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private BeanSessao beanSessao;

    public boolean isExibirBotaoHome() {
        return beanSessao.isLogado();
    }

    public String getHomeUrl() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        String ctx = ec.getRequestContextPath();
        if (beanSessao.isLogadoComoAdmin()) {
            return ctx + "/admin/home.xhtml";
        }
        if (beanSessao.isLogadoComoClinica()) {
            return ctx + "/clinica/home.xhtml";
        }
        if (beanSessao.isLogadoComoPaciente()) {
            return ctx + "/paciente/home.xhtml";
        }
        return null;
    }
}
