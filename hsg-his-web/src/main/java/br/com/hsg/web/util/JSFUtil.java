package br.com.hsg.web.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class JSFUtil {

    public static void adicionarMensagem(String clientId, FacesMessage.Severity severidade, String mensagem) {
        FacesMessage facesMessage = new FacesMessage(severidade, mensagem, null);
        FacesContext.getCurrentInstance().addMessage(clientId, facesMessage);
    }
}
