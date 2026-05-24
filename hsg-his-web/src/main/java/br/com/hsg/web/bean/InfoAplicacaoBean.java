package br.com.hsg.web.bean;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("infoApp")
@ApplicationScoped
public class InfoAplicacaoBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(InfoAplicacaoBean.class.getName());

    private String versao = "dev";
    private String dataBuild = "—";

    @PostConstruct
    public void init() {
        try (InputStream in = getClass().getResourceAsStream("/app-version.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                this.versao    = props.getProperty("app.version", versao);
                this.dataBuild = props.getProperty("app.timestamp", dataBuild);
            }
        } catch (IOException e) {
            LOG.log(Level.WARNING, "[InfoAplicacaoBean] Não foi possível carregar a versão da aplicação.", e);
        }
    }

    public String getVersao() {
        return versao;
    }

    public String getVersaoExibicao() {
        return versao != null ? versao.replace("-SNAPSHOT", "") : "dev";
    }

    public String getDataBuild() {
        return dataBuild;
    }
}
