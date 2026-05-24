package br.com.hsg.domain.entity;

import br.com.hsg.domain.converter.IndicativoStatusConverter;
import br.com.hsg.domain.enums.IndicativoStatus;
import br.com.hsg.domain.vo.Crm;
import br.com.hsg.domain.vo.Email;
import br.com.hsg.domain.vo.NomeCompleto;
import br.com.hsg.domain.vo.Telefone;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_MEDICO", schema = "hsg")
public class Medico {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MEDICO")
    @SequenceGenerator(name = "SEQ_MEDICO", sequenceName = "SEQ_MEDICO", allocationSize = 1)
    @Column(name = "ID_MEDICO")
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "primeiroNome", column = @Column(name = "FRT_NM_MEDICO", length = 100)),
            @AttributeOverride(name = "sobrenome",    column = @Column(name = "LST_NM_MEDICO", length = 150))
    })
    private NomeCompleto nome;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "DS_EMAIL_MEDICO", length = 255))
    private Email email;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "NR_TEL_MEDICO", length = 20))
    private Telefone telefone;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "numero", column = @Column(name = "NR_CRM", length = 20)),
            @AttributeOverride(name = "uf",     column = @Column(name = "UF_CRM", length = 2))
    })
    private Crm crm;

    @Getter
    @Column(name = "NR_CPF_HASH_MEDICO", unique = true, length = 64)
    private String cpfHash;

    @Column(name = "NR_CPF_ENC_MEDICO", length = 255)
    private String cpfEncrypted;

    @Getter
    @Column(name = "DT_NASC_MEDICO")
    private LocalDate dataNascimento;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ESPECIALIDADE")
    private Especialidade especialidade;

    @Getter
    @Convert(converter = IndicativoStatusConverter.class)
    @Column(name = "ST_MEDICO", nullable = false, length = 1)
    private IndicativoStatus status;

    @Getter
    @Column(name = "DT_CAD_MEDICO", nullable = false)
    private LocalDateTime dataCadastro;

    @Getter
    @Column(name = "DT_ULT_ATU_MEDICO")
    private LocalDateTime dataUltimaAtualizacao;

    @Getter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONTA_USU_MEDICO", unique = true)
    private ContaUsuario contaUsuario;

    protected Medico() {}

    public static Medico criar(
            NomeCompleto nome,
            Email email,
            Telefone telefone,
            Crm crm,
            LocalDate dataNascimento,
            Especialidade especialidade,
            ContaUsuario contaUsuario) {

        Medico m = new Medico();
        m.nome                  = nome;
        m.email                 = email;
        m.telefone              = telefone;
        m.crm                   = crm;
        m.dataNascimento        = dataNascimento;
        m.especialidade         = especialidade;
        m.contaUsuario          = contaUsuario;
        m.status                = IndicativoStatus.A;
        m.dataCadastro          = LocalDateTime.now();
        m.dataUltimaAtualizacao = LocalDateTime.now();
        return m;
    }

    public String getNomeCompleto() {
        return nome != null ? nome.getNomeCompleto() : null;
    }

    public String getEmail() {
        return email != null ? email.getValor() : null;
    }

    public String getTelefone() {
        return telefone != null ? telefone.getValor() : null;
    }

    public Crm getCrm() {
        return crm;
    }

    public void definirCpf(String cpfHash, String cpfEncrypted) {
        this.cpfHash      = cpfHash;
        this.cpfEncrypted = cpfEncrypted;
    }

    public boolean podeLogar() {
        return IndicativoStatus.A.equals(this.status);
    }
}
