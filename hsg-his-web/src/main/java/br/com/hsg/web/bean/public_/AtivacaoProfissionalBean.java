package br.com.hsg.web.bean.public_;

import br.com.hsg.domain.entity.Especialidade;
import br.com.hsg.domain.entity.PreCadastroProfissional;
import br.com.hsg.service.dto.AtivacaoFormDTO;
import br.com.hsg.service.facade.public_.AtivacaoServiceFacade;
import org.primefaces.PrimeFaces;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ViewScoped
@Named("ativacaoBean")
public class AtivacaoProfissionalBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AtivacaoProfissionalBean.class.getName());

    @Inject private AtivacaoServiceFacade ativacaoService;

    private String token;
    private String erroToken;
    private boolean concluido;
    private boolean senhasNaoConferem;

    private transient PreCadastroProfissional preCadastro;
    private transient List<Especialidade>     especialidades = Collections.emptyList();
    private final AtivacaoFormDTO form = new AtivacaoFormDTO();

    public void init() {
        if (preCadastro != null || erroToken != null) return;

        if (token == null || token.trim().isEmpty()) {
            erroToken = "Link de ativação inválido. Verifique o e-mail recebido e tente novamente.";
            return;
        }
        try {
            PreCadastroProfissional p = ativacaoService.validarToken(token.trim());
            if (p == null) {
                erroToken = "Token inválido ou não encontrado. Verifique o link recebido por e-mail.";
                return;
            }
            if (!p.isPendente()) {
                erroToken = "Este convite já foi utilizado. Seu cadastro já foi concluído anteriormente.";
                return;
            }
            if (p.isConviteExpirado()) {
                erroToken = "Este convite expirou em " + p.getDataExpiracaoConviteFormatada()
                        + ". Entre em contato com a administração do sistema para solicitar um novo convite.";
                return;
            }
            preCadastro = p;
            if (p.isMedico()) {
                especialidades = ativacaoService.listarEspecialidadesAtivas();
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Erro ao validar token de ativação", e);
            erroToken = "Não foi possível validar o convite. Tente novamente mais tarde.";
        }
    }

    public void verificarSenhas() {
        String s  = form.getSenha();
        String cs = form.getConfirmacaoSenha();
        senhasNaoConferem = s != null && cs != null && !cs.isEmpty() && !s.equals(cs);
    }

    public void confirmar() {
        if (token == null || erroToken != null || concluido) return;

        if (Boolean.TRUE.equals(senhasNaoConferem)) {
            addError("As senhas não coincidem. Verifique e tente novamente.");
            return;
        }

        try {
            ativacaoService.ativarCadastro(token.trim(), form);
            concluido = true;
            PrimeFaces.current().executeScript("PF('wDlgSucesso').show()");
        } catch (IllegalArgumentException | IllegalStateException e) {
            addError(e.getMessage());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Erro inesperado ao ativar cadastro", e);
            addError("Ocorreu um erro ao concluir o cadastro. Tente novamente mais tarde.");
        }
    }

    public String getKcLoginUrl() {
        String kcAuthUrl   = envOr("KC_AUTH_URL",    "http://localhost:8080/realms/hsg-his/protocol/openid-connect/auth");
        String clientId    = envOr("KC_CLIENT_ID",   "hsg-his-web");
        String redirectUri = envOr("KC_REDIRECT_URI","http://localhost:8180/hsg-his/callback");
        try {
            return kcAuthUrl
                    + "?response_type=code"
                    + "&client_id="    + URLEncoder.encode(clientId,    "UTF-8")
                    + "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8")
                    + "&scope=openid";
        } catch (UnsupportedEncodingException e) {
            return kcAuthUrl;
        }
    }

    private void addError(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    private static String envOr(String key, String defaultValue) {
        String v = System.getenv(key);
        return (v != null && !v.isEmpty()) ? v : defaultValue;
    }

    public String getToken()                              { return token; }
    public void   setToken(String t)                     { this.token = t; }
    public String getErroToken()                         { return erroToken; }
    public boolean isConcluido()                         { return concluido; }
    public boolean isSenhasNaoConferem()                 { return senhasNaoConferem; }
    public PreCadastroProfissional getPreCadastro()      { return preCadastro; }
    public List<Especialidade>     getEspecialidades()   { return especialidades; }
    public AtivacaoFormDTO         getForm()             { return form; }
    public boolean isPreRenderado()                      { return preCadastro != null && erroToken == null && !concluido; }
}
