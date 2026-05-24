package br.com.hsg.domain.entity;

import br.com.hsg.domain.converter.IndicativoStatusConverter;
import br.com.hsg.domain.enums.IndicativoStatus;
import br.com.hsg.domain.vo.Coren;
import br.com.hsg.domain.vo.Email;
import br.com.hsg.domain.vo.NomeCompleto;
import br.com.hsg.domain.vo.Telefone;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_ENFER", schema = "hsg")
public class Enfermeiro {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ENFER")
    @SequenceGenerator(name = "SEQ_ENFER", sequenceName = "SEQ_ENFER", allocationSize = 1)
    @Column(name = "ID_ENFER")
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "primeiroNome", column = @Column(name = "FRT_NM_ENFER", length = 100)),
            @AttributeOverride(name = "sobrenome",    column = @Column(name = "LST_NM_ENFER", length = 150))
    })
    private NomeCompleto nome;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "DS_EMAIL_ENFER", length = 255))
    private Email email;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "NR_TEL_ENFER", length = 20))
    private Telefone telefone;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "numero",    column = @Column(name = "NR_COREN",  length = 20)),
            @AttributeOverride(name = "uf",        column = @Column(name = "UF_COREN",  length = 2)),
            @AttributeOverride(name = "categoria", column = @Column(name = "CAT_COREN", length = 3))
    })
    private Coren coren;

    @Getter
    @Column(name = "NR_CPF_HASH_ENFER", unique = true, length = 64)
    private String cpfHash;

    @Column(name = "NR_CPF_ENC_ENFER", length = 255)
    private String cpfEncrypted;

    @Getter
    @Column(name = "DT_NASC_ENFER")
    private LocalDate dataNascimento;

    @Getter
    @Column(name = "DS_ESPECIALIDADE_ENFER", length = 100)
    private String especialidade;

    @Getter
    @Column(name = "DS_SETOR_ENFER", length = 100)
    private String setor;

    @Getter
    @Convert(converter = IndicativoStatusConverter.class)
    @Column(name = "ST_ENFER", nullable = false, length = 1)
    private IndicativoStatus status;

    @Getter
    @Column(name = "DT_CAD_ENFER", nullable = false)
    private LocalDateTime dataCadastro;

    @Getter
    @Column(name = "DT_ULT_ATU_ENFER")
    private LocalDateTime dataUltimaAtualizacao;

    @Getter
    @OneToOne
    @JoinColumn(name = "ID_CONTA_USU_ENFER", unique = true)
    private ContaUsuario contaUsuario;

    protected Enfermeiro() {}

    public static Enfermeiro criar(
            NomeCompleto nome,
            Email email,
            Telefone telefone,
            Coren coren,
            LocalDate dataNascimento,
            String especialidade,
            String setor,
            ContaUsuario contaUsuario) {

        Enfermeiro e = new Enfermeiro();
        e.nome               = nome;
        e.email              = email;
        e.telefone           = telefone;
        e.coren              = coren;
        e.dataNascimento     = dataNascimento;
        e.especialidade      = especialidade;
        e.setor              = setor;
        e.contaUsuario       = contaUsuario;
        e.status             = IndicativoStatus.A;
        e.dataCadastro       = LocalDateTime.now();
        e.dataUltimaAtualizacao = LocalDateTime.now();
        return e;
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

    public Coren getCoren() {
        return coren;
    }

    public void definirCpf(String cpfHash, String cpfEncrypted) {
        this.cpfHash      = cpfHash;
        this.cpfEncrypted = cpfEncrypted;
    }

    public boolean podeLogar() {
        return IndicativoStatus.A.equals(this.status);
    }
}
