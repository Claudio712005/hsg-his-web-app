package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.TipoResponsavel;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_CONSULTA_ANOTACAO", schema = "hsg")
public class ConsultaAnotacao {

    public static final int MAX_TITULO    = 200;
    public static final int MAX_DESCRICAO = 2000;

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CONSULTA_ANOTACAO")
    @SequenceGenerator(name = "SEQ_CONSULTA_ANOTACAO",
            sequenceName = "SEQ_CONSULTA_ANOTACAO", allocationSize = 1)
    @Column(name = "ID_CONSULTA_ANOTACAO")
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONSULTA", nullable = false)
    private Consulta consulta;

    @Getter
    @Column(name = "DS_TITULO", length = MAX_TITULO, nullable = false)
    private String titulo;

    @Getter
    @Column(name = "DS_DESCRICAO", length = MAX_DESCRICAO, nullable = false)
    private String descricao;

    @Getter
    @Column(name = "ID_RESPONSAVEL", nullable = false)
    private Long idResponsavel;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_RESPONSAVEL", length = 12, nullable = false)
    private TipoResponsavel tipoResponsavel;

    @Getter
    @Column(name = "DT_CRIACAO", nullable = false)
    private LocalDateTime dataCriacao;

    protected ConsultaAnotacao() {}

    public static ConsultaAnotacao registrar(Consulta consulta, String titulo, String descricao,
                                              Long idResponsavel, TipoResponsavel tipoResponsavel) {
        if (consulta == null) {
            throw new IllegalArgumentException("A consulta é obrigatória.");
        }
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("O título é obrigatório.");
        }
        if (titulo.length() > MAX_TITULO) {
            throw new IllegalArgumentException("O título deve ter no máximo " + MAX_TITULO + " caracteres.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição é obrigatória.");
        }
        if (descricao.length() > MAX_DESCRICAO) {
            throw new IllegalArgumentException("A descrição deve ter no máximo " + MAX_DESCRICAO + " caracteres.");
        }
        if (idResponsavel == null) {
            throw new IllegalArgumentException("O responsável é obrigatório.");
        }
        if (tipoResponsavel == null) {
            throw new IllegalArgumentException("O tipo do responsável é obrigatório.");
        }
        ConsultaAnotacao a = new ConsultaAnotacao();
        a.consulta         = consulta;
        a.titulo           = titulo.trim();
        a.descricao        = descricao.trim();
        a.idResponsavel    = idResponsavel;
        a.tipoResponsavel  = tipoResponsavel;
        a.dataCriacao      = LocalDateTime.now();
        return a;
    }
}
