package br.com.hsg.web.bean.clinica;

import br.com.hsg.web.bean.session.BeanSessao;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;

@ViewScoped
@Named("painelClinicaBean")
public class PainelClinicaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private BeanSessao beanSessao;

    public void sair() throws IOException {
        beanSessao.encerrarSessao();
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.invalidateSession();
        ec.redirect(ec.getRequestContextPath() + "/home.xhtml");
    }
}
