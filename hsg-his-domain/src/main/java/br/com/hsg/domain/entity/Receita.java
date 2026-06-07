package br.com.hsg.domain.entity;

import br.com.hsg.domain.converter.IndicativoStatusConverter;
import br.com.hsg.domain.enums.IndicativoStatus;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "TB_RECEITA", schema = "hsg")
public class Receita {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_RECEITA")
    @SequenceGenerator(name = "SEQ_RECEITA", sequenceName = "SEQ_RECEITA", allocationSize = 1)
    @Column(name = "ID_RECEITA")
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONSULTA", nullable = false)
    private Consulta consulta;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MEDICO", nullable = false)
    private Medico medico;

    @Getter
    @Column(name = "DT_EMISSAO", nullable = false)
    private LocalDateTime dataEmissao;

    @Getter
    @Convert(converter = IndicativoStatusConverter.class)
    @Column(name = "ST_RECEITA", length = 1, nullable = false)
    private IndicativoStatus status;

    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    @OrderBy("ordem ASC")
    private List<ReceitaItem> itens = new ArrayList<>();

    protected Receita() {}

    public static Receita emitir(Consulta consulta, Medico medico, List<ReceitaItem> itens) {
        if (consulta == null) {
            throw new IllegalArgumentException("Consulta é obrigatória.");
        }
        if (medico == null) {
            throw new IllegalArgumentException("Médico é obrigatório.");
        }
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("É necessário ao menos um item.");
        }
        Receita r = new Receita();
        r.consulta    = consulta;
        r.medico      = medico;
        r.dataEmissao = LocalDateTime.now();
        r.status      = IndicativoStatus.A;
        int ordem = 1;
        for (ReceitaItem it : itens) {
            if (it == null) {
                throw new IllegalArgumentException("Item inválido.");
            }
            it.vincular(r);
            r.itens.add(it);
            ordem++;
        }
        return r;
    }

    public List<ReceitaItem> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public void inativar() {
        this.status = IndicativoStatus.I;
    }
}
