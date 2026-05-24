package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.Estado;
import br.com.hsg.domain.enums.StatusSolicitacao;
import br.com.hsg.domain.enums.TipoCancelador;
import br.com.hsg.domain.enums.TipoSolicitacao;
import br.com.hsg.domain.vo.DataNascimento;
import br.com.hsg.domain.vo.Email;
import br.com.hsg.domain.vo.NomeCompleto;
import br.com.hsg.domain.vo.Telefone;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_SOLIC_ATL", schema = "hsg")
public class SolicitacaoAtualizacao {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SOLIC_ATL")
    @SequenceGenerator(name = "SEQ_SOLIC_ATL", sequenceName = "SEQ_SOLIC_ATL", allocationSize = 1)
    @Column(name = "ID_SOLIC_ATL")
    private Long id;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_SOLIC", length = 10, nullable = false)
    private TipoSolicitacao tipoSolicitacao;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "primeiroNome", column = @Column(name = "FRT_NM_SOLIC_ATL")),
            @AttributeOverride(name = "sobrenome",    column = @Column(name = "LST_NM_SOLIC_ATL"))
    })
    private NomeCompleto nomeCompleto;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "DS_EMAIL_ATL"))
    private Email email;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "NR_TEL_ATL"))
    private Telefone telefone;

    @Column(name = "SNP_NOME_COMPLETO", length = 250)
    private String snapshotNomeCompleto;

    @Column(name = "SNP_EMAIL", length = 255)
    private String snapshotEmail;

    @Column(name = "SNP_TEL", length = 20)
    private String snapshotTelefone;

    @Getter
    @Column(name = "DS_LOGR_PROP", length = 200)
    private String logradouroProposto;

    @Getter
    @Column(name = "NR_PROP", length = 10)
    private String numeroProposto;

    @Getter
    @Column(name = "DS_COMPL_PROP", length = 100)
    private String complementoProposto;

    @Getter
    @Column(name = "DS_BAIRRO_PROP", length = 100)
    private String bairroProposto;

    @Getter
    @Column(name = "DS_CIDADE_PROP", length = 100)
    private String cidadeProposta;

    @Getter
    @Column(name = "SG_ESTADO_PROP", length = 2)
    private String estadoProposto;

    @Getter
    @Column(name = "NR_CEP_PROP", length = 8)
    private String cepProposto;

    @Getter
    @Column(name = "SNP_LOGR", length = 200)
    private String snapshotLogradouro;

    @Getter
    @Column(name = "SNP_NR", length = 10)
    private String snapshotNumero;

    @Getter
    @Column(name = "SNP_COMPL", length = 100)
    private String snapshotComplemento;

    @Getter
    @Column(name = "SNP_BAIRRO", length = 100)
    private String snapshotBairro;

    @Getter
    @Column(name = "SNP_CIDADE", length = 100)
    private String snapshotCidade;

    @Getter
    @Column(name = "SNP_ESTADO", length = 2)
    private String snapshotEstado;

    @Getter
    @Column(name = "SNP_CEP", length = 8)
    private String snapshotCep;

    @Getter
    @Column(name = "VL_PESO_PROP")
    private Double pesoProposto;

    @Getter
    @Column(name = "VL_ALTURA_PROP")
    private Double alturaProposta;

    @Getter
    @Column(name = "TP_SANG_PROP", length = 10)
    private String tipoSanguineoProposto;

    @Getter
    @Column(name = "SNP_PESO")
    private Double snapshotPeso;

    @Getter
    @Column(name = "SNP_ALTURA")
    private Double snapshotAltura;

    @Getter
    @Column(name = "SNP_TP_SANG", length = 10)
    private String snapshotTipoSanguineo;

    @Getter
    @Column(name = "DS_MOTIVO", length = 500)
    private String motivo;

    @Getter
    @Column(name = "DT_CAD_SOLIC_ATL", nullable = false)
    private LocalDateTime dataCadastro;

    @Getter
    @Column(name = "DT_ULT_ATU")
    private LocalDateTime dataUltimaAtualizacao;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "SIT_APR_SOLIC", length = 1, nullable = false)
    private StatusSolicitacao status;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PAC", nullable = false)
    private Paciente paciente;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENFER")
    private Enfermeiro enfermeiro;

    @Getter
    @Column(name = "DS_MOT_CANCEL", length = 500)
    private String motivoCancelamento;

    @Getter
    @Column(name = "ID_CANCELADOR")
    private Long idCancelador;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_CANCELADOR", length = 10)
    private TipoCancelador tipoCancelador;

    @Getter
    @Column(name = "DT_CANCELAMENTO")
    private LocalDateTime dataCancelamento;

    protected SolicitacaoAtualizacao() {}

    public static SolicitacaoAtualizacao solicitarCadastral(
            Paciente paciente,
            NomeCompleto novoNome,
            Email novoEmail,
            Telefone novoTelefone,
            String motivo
    ) {
        Paciente.validar(
                novoNome,
                novoEmail,
                new DataNascimento(paciente.getDataNascimento()),
                novoTelefone
        );

        SolicitacaoAtualizacao s = base(paciente, motivo, TipoSolicitacao.CADASTRAL);
        s.nomeCompleto         = novoNome;
        s.email                = novoEmail;
        s.telefone             = novoTelefone;
        s.snapshotNomeCompleto = paciente.getNomeCompleto();
        s.snapshotEmail        = paciente.getEmail();
        s.snapshotTelefone     = paciente.getTelefone();
        return s;
    }

    public static SolicitacaoAtualizacao solicitarEndereco(
            Paciente paciente,
            Endereco enderecoAtual,
            String logradouro, String numero, String complemento,
            String bairro, String cidade, Estado estado, String cep,
            String motivo
    ) {
        Endereco.validar(
                logradouro,
                numero,
                bairro,
                estado,
                cep
        );

        SolicitacaoAtualizacao s = base(paciente, motivo, TipoSolicitacao.ENDERECO);
        s.logradouroProposto  = logradouro;
        s.numeroProposto      = numero;
        s.complementoProposto = complemento;
        s.bairroProposto      = bairro;
        s.cidadeProposta      = cidade;
        s.estadoProposto      = estado != null ? estado.getSigla() : null;
        s.cepProposto         = cep;
        if (enderecoAtual != null) {
            s.snapshotLogradouro  = enderecoAtual.getLogradouro();
            s.snapshotNumero      = enderecoAtual.getNumero();
            s.snapshotComplemento = enderecoAtual.getComplemento();
            s.snapshotBairro      = enderecoAtual.getBairro();
            s.snapshotCidade      = enderecoAtual.getCidade();
            s.snapshotEstado      = enderecoAtual.getEstado() != null ? enderecoAtual.getEstado().getSigla() : null;
            s.snapshotCep         = enderecoAtual.getCep();
        }
        return s;
    }

    public static SolicitacaoAtualizacao solicitarClinica(
            Paciente paciente,
            Double snapshotPeso, Double snapshotAltura, String snapshotTipoSang,
            Double pesoProposto, Double alturaProposta, String tipoSanguineoProposto,
            String motivo
    ) {
        Paciente.validarDadosClinicos(
                pesoProposto, alturaProposta, tipoSanguineoProposto,
                motivo
        );

        SolicitacaoAtualizacao s = base(paciente, motivo, TipoSolicitacao.CLINICO);
        s.pesoProposto          = pesoProposto;
        s.alturaProposta        = alturaProposta;
        s.tipoSanguineoProposto = tipoSanguineoProposto;
        s.snapshotPeso          = snapshotPeso;
        s.snapshotAltura        = snapshotAltura;
        s.snapshotTipoSanguineo = snapshotTipoSang;
        return s;
    }

    public void cancelar(Long idCancelador, TipoCancelador tipoCancelador, String motivoCancelamento) {
        if (this.status != StatusSolicitacao.P) {
            throw new IllegalStateException("Apenas solicitações pendentes podem ser canceladas.");
        }
        if (motivoCancelamento == null || motivoCancelamento.trim().isEmpty()) {
            throw new IllegalArgumentException("Motivo do cancelamento é obrigatório.");
        }
        this.status                = StatusSolicitacao.C;
        this.idCancelador          = idCancelador;
        this.tipoCancelador        = tipoCancelador;
        this.motivoCancelamento    = motivoCancelamento.trim();
        this.dataCancelamento      = LocalDateTime.now();
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    private static SolicitacaoAtualizacao base(Paciente paciente, String motivo, TipoSolicitacao tipo) {
        SolicitacaoAtualizacao s = new SolicitacaoAtualizacao();
        s.paciente        = paciente;
        s.motivo          = motivo;
        s.tipoSolicitacao = tipo;
        s.status          = StatusSolicitacao.P;
        s.dataCadastro    = LocalDateTime.now();
        return s;
    }

    public String getNomeCompleto() {
        return nomeCompleto != null ? nomeCompleto.getNomeCompleto() : null;
    }

    public String getEmail() {
        return email != null ? email.getValor() : null;
    }

    public String getTelefone() {
        return telefone != null ? telefone.getValor() : null;
    }

    public String getSnapshotNomeCompleto() { return snapshotNomeCompleto; }
    public String getSnapshotEmail()        { return snapshotEmail; }
    public String getSnapshotTelefone()     { return snapshotTelefone; }
}
