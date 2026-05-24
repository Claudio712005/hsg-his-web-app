package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.StatusEnvioConvite;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "TB_ENV_CONV_HIST", schema = "hsg")
public class EnvioConviteHistorico {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ENV_CONV_HIST")
    @SequenceGenerator(name = "SEQ_ENV_CONV_HIST", sequenceName = "SEQ_ENV_CONV_HIST", allocationSize = 1)
    @Column(name = "ID_ENV_CONV_HIST")
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PRE_CAD_PROF", nullable = false)
    private PreCadastroProfissional preCadastro;

    @Getter
    @Column(name = "ID_ADM_REMETENTE", nullable = false)
    private Long idAdminRemetente;

    @Getter
    @Column(name = "NM_ADM_REMETENTE", nullable = false, length = 255)
    private String nomeAdminRemetente;

    @Getter
    @Column(name = "DT_ENVIO", nullable = false)
    private LocalDateTime dataEnvio;

    @Getter
    @Column(name = "DT_EXPIRACAO", nullable = false)
    private LocalDateTime dataExpiracao;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "ST_ENVIO_CONVITE", nullable = false, length = 10)
    private StatusEnvioConvite status;

    @Getter
    @Column(name = "DS_ERRO_ENVIO", length = 1000)
    private String mensagemErro;

    protected EnvioConviteHistorico() {}

    public static EnvioConviteHistorico registrarEnvio(
            PreCadastroProfissional preCadastro,
            Long idAdmin,
            String nomeAdmin,
            LocalDateTime dataExpiracao) {

        EnvioConviteHistorico h = new EnvioConviteHistorico();
        h.preCadastro         = preCadastro;
        h.idAdminRemetente    = idAdmin;
        h.nomeAdminRemetente  = nomeAdmin;
        h.dataEnvio           = LocalDateTime.now();
        h.dataExpiracao       = dataExpiracao;
        h.status              = StatusEnvioConvite.ENVIADO;
        return h;
    }

    public static EnvioConviteHistorico registrarErro(
            PreCadastroProfissional preCadastro,
            Long idAdmin,
            String nomeAdmin,
            LocalDateTime dataExpiracao,
            String mensagemErro) {

        EnvioConviteHistorico h = new EnvioConviteHistorico();
        h.preCadastro         = preCadastro;
        h.idAdminRemetente    = idAdmin;
        h.nomeAdminRemetente  = nomeAdmin;
        h.dataEnvio           = LocalDateTime.now();
        h.dataExpiracao       = dataExpiracao;
        h.status              = StatusEnvioConvite.ERRO;
        h.mensagemErro        = mensagemErro;
        return h;
    }

    public void marcarExpirado() {
        this.status = StatusEnvioConvite.EXPIRADO;
    }

    public void marcarAceito() {
        this.status = StatusEnvioConvite.ACEITO;
    }

    public boolean isExpirado() {
        return LocalDateTime.now().isAfter(dataExpiracao);
    }

    public String getDataEnvioFormatada() {
        if (dataEnvio == null) return "";
        return dataEnvio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getDataExpiracaoFormatada() {
        if (dataExpiracao == null) return "";
        return dataExpiracao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getStatusCssClass() {
        if (status == null) return "";
        switch (status) {
            case ENVIADO:  return "ativo";
            case ERRO:     return "inativo";
            case EXPIRADO: return "suspenso";
            case ACEITO:   return "confirm";
            default:       return "";
        }
    }
}
