package br.com.hsg.domain.entity;

import br.com.hsg.domain.converter.IndicativoStatusConverter;
import br.com.hsg.domain.enums.IndicativoStatus;
import br.com.hsg.domain.vo.DataNascimento;
import br.com.hsg.domain.vo.Email;
import br.com.hsg.domain.vo.NomeCompleto;
import br.com.hsg.domain.vo.Telefone;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_PAC", schema = "hsg")
public class Paciente {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PAC")
    @SequenceGenerator(name = "SEQ_PAC", sequenceName = "SEQ_PAC", allocationSize = 1)
    @Column(name = "ID_PAC")
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "primeiroNome", column = @Column(name = "FRT_NM_PAC")),
            @AttributeOverride(name = "sobrenome", column = @Column(name = "LST_NM_PAC"))
    })
    private NomeCompleto nome;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "DS_EMAIL"))
    private Email email;

    @Column(name = "NR_CPF_HASH", nullable = false, unique = true, length = 64)
    private String cpfHash;

    @Column(name = "NR_CPF_ENC", nullable = false, length = 255)
    private String cpfEncrypted;

    @Column(name = "NR_RG_ENC", length = 255)
    private String rgEncrypted;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "DT_NASC_PAC"))
    private DataNascimento dataNascimento;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "NR_TEL"))
    private Telefone telefone;

    @Getter
    @Convert(converter = IndicativoStatusConverter.class)
    @Column(name = "ST_PAC", nullable = false, length = 1)
    private IndicativoStatus status;

    @Getter
    @Column(name = "DT_CAD_PAC", nullable = false)
    private LocalDateTime dataCadastro;

    @Getter
    @Column(name = "DT_ULT_ATU")
    private LocalDateTime dataUltimaAtualizacao;

    @Getter
    @OneToOne
    @JoinColumn(name = "ID_CONTA_USU", referencedColumnName = "ID_CONTA_USU")
    private ContaUsuario contaUsuario;

    protected Paciente() {}

    public static Paciente criar(
            NomeCompleto nome,
            Email email,
            DataNascimento dataNascimento,
            Telefone telefone
    ){
        Paciente paciente = new Paciente();
        validar(nome, email, dataNascimento, telefone);

        paciente.nome = nome;
        paciente.email = email;
        paciente.dataNascimento = dataNascimento;
        paciente.telefone = telefone;
        paciente.dataCadastro = LocalDateTime.now();
        paciente.status = IndicativoStatus.A;
        return paciente;
    }

    public static void validar(
            NomeCompleto nome,
            Email email,
            DataNascimento dataNascimento,
            Telefone telefone
    ){
        if(nome == null){
            throw new IllegalArgumentException("O nome completo é obrigatório.");
        }

        if(email == null){
            throw new IllegalArgumentException("O email é obrigatório.");
        }

        if(dataNascimento == null){
            throw new IllegalArgumentException("A data de nascimento é obrigatória.");
        }

        if(telefone == null){
            throw new IllegalArgumentException("O telefone é obrigatório.");
        }

    }

    public static void validarDadosClinicos(
            Double pesoProposto, Double alturaProposta, String tipoSanguineoProposto,
            String motivo
    ){
        if(pesoProposto == null){
            throw new IllegalArgumentException("O peso proposto é obrigatório.");
        }

        if(pesoProposto <= 0){
            throw new IllegalArgumentException("O peso proposto deve ser um valor positivo.");
        }

        if(alturaProposta == null){
            throw new IllegalArgumentException("A altura proposta é obrigatória.");
        }

        if(alturaProposta <= 0){
            throw new IllegalArgumentException("A altura proposta deve ser um valor positivo.");
        }

        if(tipoSanguineoProposto == null || tipoSanguineoProposto.isEmpty()){
            throw new IllegalArgumentException("O tipo sanguíneo proposto é obrigatório.");
        }
    }

    public boolean podeLogar(){
        return IndicativoStatus.A.equals(this.status);
    }

    public boolean isAtivo() {
        return this.status == IndicativoStatus.A;
    }

    public void ativar() {
        this.status = IndicativoStatus.A;
    }

    public void inativar() {
        this.status = IndicativoStatus.I;
    }

    public void definirCpf(String cpfHash, String cpfEncrypted) {
        this.cpfHash = cpfHash;
        this.cpfEncrypted = cpfEncrypted;
    }

    public void definirRg(String rgEncrypted) {
        this.rgEncrypted = rgEncrypted;
    }

    public String getNomeCompleto() {
        return this.nome != null ? this.nome.getNomeCompleto() : null;
    }

    public String getEmail() {
        return this.email != null ? this.email.getValor() : null;
    }

    public LocalDate getDataNascimento() {
        return this.dataNascimento != null ? this.dataNascimento.getValor() : null;
    }

    public String getTelefone() {
        return this.telefone != null ? this.telefone.getValor() : null;
    }

    public void atualizarDadosPessoais(
            NomeCompleto nome
    ) {
        this.nome = nome;
        atualizarDataModificacao();
    }

    public void atualizarContato(Email email, Telefone telefone) {
        this.email = email;
        this.telefone = telefone;
        atualizarDataModificacao();
    }

    public void atualizarDataModificacao(){
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }
}