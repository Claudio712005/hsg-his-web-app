package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.CategoriaCoren;
import br.com.hsg.domain.enums.StatusPreCadastro;
import br.com.hsg.domain.enums.TipoProfissional;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(name = "TB_PRE_CAD_PROF", schema = "hsg")
public class PreCadastroProfissional {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PRE_CAD_PROF")
    @SequenceGenerator(name = "SEQ_PRE_CAD_PROF", sequenceName = "SEQ_PRE_CAD_PROF", allocationSize = 1)
    @Column(name = "ID_PRE_CAD_PROF")
    private Long id;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_PROFISSIONAL", nullable = false, length = 12)
    private TipoProfissional tipoProfissional;

    @Getter
    @Column(name = "NM_PROFISSIONAL", nullable = false, length = 255)
    private String nome;

    @Getter
    @Column(name = "DS_EMAIL_PESSOAL", nullable = false, length = 255)
    private String emailPessoal;

    @Getter
    @Column(name = "DS_EMAIL_CORP", length = 255, unique = true)
    private String emailCorporativo;

    @Getter
    @Column(name = "NR_CPF_PROF", nullable = false, length = 14)
    private String cpf;

    @Getter
    @Column(name = "NR_CRM_PROF", length = 20)
    private String crm;

    @Getter
    @Column(name = "UF_CRM_PROF", length = 2)
    private String ufCrm;

    @Getter
    @Column(name = "DS_ESPECIALIDADE_PROF", length = 100)
    private String especialidade;

    @Getter
    @Column(name = "NR_COREN_PROF", length = 20)
    private String coren;

    @Getter
    @Column(name = "UF_COREN_PROF", length = 2)
    private String ufCoren;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "CAT_COREN_PROF", length = 3)
    private CategoriaCoren categoriaCoren;

    @Getter
    @Column(name = "ID_ADM_CRIADOR", nullable = false)
    private Long idAdminCriador;

    @Getter
    @Column(name = "DT_CRIACAO", nullable = false)
    private LocalDateTime dataCriacao;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "ST_PRE_CAD", nullable = false, length = 10)
    private StatusPreCadastro status;

    @Getter
    @Column(name = "TOKEN_CONVITE", nullable = false, unique = true, length = 36)
    private String tokenConvite;

    @Getter
    @Column(name = "FL_EMAIL_ENVIADO", nullable = false)
    private boolean emailEnviado;

    @Getter
    @Column(name = "DT_ENVIO_EMAIL")
    private LocalDateTime dataEnvioEmail;

    @Getter
    @Column(name = "DT_EXPIRACAO_CONVITE")
    private LocalDateTime dataExpiracaoConvite;

    @Getter
    @Column(name = "QT_ENVIOS", nullable = false)
    private int quantidadeEnvios;

    protected PreCadastroProfissional() {}

    public static PreCadastroProfissional criarParaMedico(
            String nome,
            String emailPessoal,
            String cpf,
            String crm,
            String ufCrm,
            String especialidade,
            Long idAdminCriador) {

        PreCadastroProfissional p = basico(nome, emailPessoal, cpf, TipoProfissional.MEDICO, idAdminCriador);
        p.crm          = crm;
        p.ufCrm        = ufCrm;
        p.especialidade = especialidade;
        return p;
    }

    public static PreCadastroProfissional criarParaEnfermeiro(
            String nome,
            String emailPessoal,
            String cpf,
            String coren,
            String ufCoren,
            CategoriaCoren categoriaCoren,
            Long idAdminCriador) {

        PreCadastroProfissional p = basico(nome, emailPessoal, cpf, TipoProfissional.ENFERMEIRO, idAdminCriador);
        p.coren         = coren;
        p.ufCoren       = ufCoren;
        p.categoriaCoren = categoriaCoren;
        return p;
    }

    private static PreCadastroProfissional basico(
            String nome, String emailPessoal, String cpf,
            TipoProfissional tipo, Long idAdminCriador) {

        PreCadastroProfissional p = new PreCadastroProfissional();
        p.nome            = nome;
        p.emailPessoal    = emailPessoal;
        p.cpf             = cpf;
        p.tipoProfissional = tipo;
        p.idAdminCriador  = idAdminCriador;
        p.dataCriacao     = LocalDateTime.now();
        p.status          = StatusPreCadastro.PENDENTE;
        p.tokenConvite    = UUID.randomUUID().toString();
        p.emailEnviado    = false;
        return p;
    }

    public void definirEmailCorporativo(String emailCorporativo) {
        this.emailCorporativo = emailCorporativo;
    }

    public LocalDateTime registrarEnvioEmail(int diasExpiracao) {
        this.tokenConvite          = UUID.randomUUID().toString();
        LocalDateTime expiracao    = LocalDateTime.now().plusDays(diasExpiracao);
        this.emailEnviado          = true;
        this.dataEnvioEmail        = LocalDateTime.now();
        this.dataExpiracaoConvite  = expiracao;
        this.quantidadeEnvios      = this.quantidadeEnvios + 1;
        return expiracao;
    }

    public boolean isConviteExpirado() {
        return dataExpiracaoConvite != null && LocalDateTime.now().isAfter(dataExpiracaoConvite);
    }

    public void concluir() {
        this.status = StatusPreCadastro.CONCLUIDO;
    }

    public void expirar() {
        this.status = StatusPreCadastro.EXPIRADO;
    }

    public boolean isPendente() {
        return StatusPreCadastro.PENDENTE.equals(this.status);
    }

    public boolean isMedico() {
        return TipoProfissional.MEDICO.equals(this.tipoProfissional);
    }

    public boolean isEnfermeiro() {
        return TipoProfissional.ENFERMEIRO.equals(this.tipoProfissional);
    }

    public String getDataCriacaoFormatada() {
        if (dataCriacao == null) return "";
        return dataCriacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getDataEnvioEmailFormatada() {
        if (dataEnvioEmail == null) return "";
        return dataEnvioEmail.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getDataExpiracaoConviteFormatada() {
        if (dataExpiracaoConvite == null) return "—";
        return dataExpiracaoConvite.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getCrmFormatado() {
        if (crm == null || ufCrm == null) return "—";
        return "CRM-" + ufCrm + " " + crm;
    }

    public String getCorenFormatado() {
        if (coren == null || ufCoren == null) return "—";
        String cat = categoriaCoren != null ? "/" + categoriaCoren.name() : "";
        return "COREN-" + ufCoren + " " + coren + cat;
    }
}
