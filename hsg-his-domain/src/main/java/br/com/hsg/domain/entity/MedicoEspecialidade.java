package br.com.hsg.domain.entity;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_MEDICO_ESPECIALIDADE", schema = "hsg",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ID_MEDICO", "ID_ESPECIALIDADE"}))
public class MedicoEspecialidade {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MEDICO_ESPECIALIDADE")
    @SequenceGenerator(name = "SEQ_MEDICO_ESPECIALIDADE",
            sequenceName = "SEQ_MEDICO_ESPECIALIDADE", allocationSize = 1)
    @Column(name = "ID_MEDICO_ESPECIALIDADE")
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MEDICO", nullable = false)
    private Medico medico;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ESPECIALIDADE", nullable = false)
    private Especialidade especialidade;

    @Column(name = "ST_PRINCIPAL", length = 1, nullable = false)
    private String principal;

    @Getter
    @Column(name = "DT_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;

    protected MedicoEspecialidade() {}

    public static MedicoEspecialidade criar(Medico medico, Especialidade especialidade, boolean principal) {
        if (medico == null) {
            throw new IllegalArgumentException("O médico é obrigatório.");
        }
        if (especialidade == null) {
            throw new IllegalArgumentException("A especialidade é obrigatória.");
        }
        MedicoEspecialidade me = new MedicoEspecialidade();
        me.medico        = medico;
        me.especialidade = especialidade;
        me.principal     = principal ? "S" : "N";
        me.dataCadastro  = LocalDateTime.now();
        return me;
    }

    public boolean isPrincipal() {
        return "S".equalsIgnoreCase(principal);
    }

    public void marcarComoPrincipal() {
        this.principal = "S";
    }

    public void desmarcarPrincipal() {
        this.principal = "N";
    }
}
