package br.com.hsg.domain.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "TB_RECEITA_ITEM", schema = "hsg")
public class ReceitaItem {

    public static final int MAX_MEDICAMENTO = 300;
    public static final int MAX_POSOLOGIA   = 500;
    public static final int MAX_OBSERVACAO  = 1000;
    public static final int MAX_CID_10      = 10;

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_RECEITA_ITEM")
    @SequenceGenerator(name = "SEQ_RECEITA_ITEM",
            sequenceName = "SEQ_RECEITA_ITEM", allocationSize = 1)
    @Column(name = "ID_RECEITA_ITEM")
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RECEITA", nullable = false)
    private Receita receita;

    @Getter
    @Column(name = "DS_MEDICAMENTO", length = MAX_MEDICAMENTO, nullable = false)
    private String medicamento;

    @Getter
    @Column(name = "DS_POSOLOGIA", length = MAX_POSOLOGIA, nullable = false)
    private String posologia;

    @Getter
    @Column(name = "DS_OBSERVACAO", length = MAX_OBSERVACAO)
    private String observacao;

    @Getter
    @Column(name = "DS_CID_10", length = MAX_CID_10)
    private String cid10;

    @Getter
    @Column(name = "NR_ORDEM", nullable = false)
    private int ordem;

    protected ReceitaItem() {}

    public static ReceitaItem criar(String medicamento, String posologia, String observacao,
                                      String cid10, int ordem) {
        if (medicamento == null || medicamento.trim().isEmpty()) {
            throw new IllegalArgumentException("Medicamento é obrigatório.");
        }
        if (medicamento.length() > MAX_MEDICAMENTO) {
            throw new IllegalArgumentException(
                    "Medicamento deve ter no máximo " + MAX_MEDICAMENTO + " caracteres.");
        }
        if (posologia == null || posologia.trim().isEmpty()) {
            throw new IllegalArgumentException("Posologia é obrigatória.");
        }
        if (posologia.length() > MAX_POSOLOGIA) {
            throw new IllegalArgumentException(
                    "Posologia deve ter no máximo " + MAX_POSOLOGIA + " caracteres.");
        }
        if (observacao != null && observacao.length() > MAX_OBSERVACAO) {
            throw new IllegalArgumentException(
                    "Observação deve ter no máximo " + MAX_OBSERVACAO + " caracteres.");
        }
        if (cid10 != null && cid10.length() > MAX_CID_10) {
            throw new IllegalArgumentException(
                    "CID-10 deve ter no máximo " + MAX_CID_10 + " caracteres.");
        }
        if (ordem < 1) {
            throw new IllegalArgumentException("Ordem deve ser >= 1.");
        }
        ReceitaItem ri = new ReceitaItem();
        ri.medicamento = medicamento.trim();
        ri.posologia   = posologia.trim();
        ri.observacao  = (observacao == null || observacao.trim().isEmpty()) ? null : observacao.trim();
        ri.cid10       = (cid10 == null || cid10.trim().isEmpty()) ? null : cid10.trim().toUpperCase();
        ri.ordem       = ordem;
        return ri;
    }

    void vincular(Receita r) { this.receita = r; }
}
