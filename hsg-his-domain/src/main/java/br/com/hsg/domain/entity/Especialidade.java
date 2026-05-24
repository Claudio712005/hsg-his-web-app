package br.com.hsg.domain.entity;

import br.com.hsg.domain.converter.IndicativoStatusConverter;
import br.com.hsg.domain.enums.IndicativoStatus;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "TB_ESPECIALIDADE", schema = "hsg")
public class Especialidade {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ESPECIALIDADE")
    @SequenceGenerator(name = "SEQ_ESPECIALIDADE", sequenceName = "SEQ_ESPECIALIDADE", allocationSize = 1)
    @Column(name = "ID_ESPECIALIDADE")
    private Long id;

    @Getter
    @Column(name = "NM_ESPECIALIDADE", length = 100, nullable = false, unique = true)
    private String nome;

    @Getter
    @Column(name = "DS_ESPECIALIDADE", length = 500)
    private String descricao;

    @Getter
    @Column(name = "AREA_ESPECIALIDADE", length = 50)
    private String area;

    @Getter
    @Convert(converter = IndicativoStatusConverter.class)
    @Column(name = "ST_ESPECIALIDADE", nullable = false, length = 1)
    private IndicativoStatus status;

    @Getter
    @Column(name = "DT_CAD_ESPECIALIDADE", nullable = false)
    private LocalDateTime dataCadastro;

    protected Especialidade() {}

    public static Especialidade criar(String nome, String descricao, String area) {
        Objects.requireNonNull(nome, "Nome da especialidade é obrigatório.");
        if (nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da especialidade não pode ser vazio.");
        }
        Especialidade e = new Especialidade();
        e.nome         = nome.trim();
        e.descricao    = descricao;
        e.area         = area;
        e.status       = IndicativoStatus.A;
        e.dataCadastro = LocalDateTime.now();
        return e;
    }

    public boolean isAtiva() {
        return IndicativoStatus.A.equals(status);
    }
}
