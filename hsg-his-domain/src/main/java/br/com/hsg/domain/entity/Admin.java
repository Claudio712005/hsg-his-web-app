package br.com.hsg.domain.entity;

import br.com.hsg.domain.converter.IndicativoStatusConverter;
import br.com.hsg.domain.enums.IndicativoStatus;
import br.com.hsg.domain.vo.Email;
import br.com.hsg.domain.vo.NomeCompleto;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "TB_ADM", schema = "hsg")
public class Admin {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ADM")
    @SequenceGenerator(name = "SEQ_ADM", sequenceName = "SEQ_ADM", allocationSize = 1)
    @Column(name = "ID_ADM")
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "primeiroNome", column = @Column(name = "FRT_NM_ADM", length = 100)),
            @AttributeOverride(name = "sobrenome",    column = @Column(name = "LST_NM_ADM", length = 150))
    })
    private NomeCompleto nome;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "DS_EMAIL_ADM", length = 255))
    private Email email;

    @Getter
    @Convert(converter = IndicativoStatusConverter.class)
    @Column(name = "ST_ADM", nullable = false, length = 1)
    private IndicativoStatus status;

    @Getter
    @Column(name = "DT_CAD_ADM", nullable = false)
    private LocalDateTime dataCadastro;

    @Getter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONTA_USU_ADM", unique = true)
    private ContaUsuario contaUsuario;

    protected Admin() {}

    public static Admin criar(NomeCompleto nome, Email email, ContaUsuario contaUsuario) {
        Objects.requireNonNull(contaUsuario, "ContaUsuario é obrigatória.");
        Admin a = new Admin();
        a.nome         = nome;
        a.email        = email;
        a.contaUsuario = contaUsuario;
        a.status       = IndicativoStatus.A;
        a.dataCadastro = LocalDateTime.now();
        return a;
    }

    public String getNomeCompleto() {
        return nome != null ? nome.getNomeCompleto() : null;
    }

    public String getEmail() {
        return email != null ? email.getValor() : null;
    }

    public boolean podeLogar() {
        return IndicativoStatus.A.equals(status);
    }
}
