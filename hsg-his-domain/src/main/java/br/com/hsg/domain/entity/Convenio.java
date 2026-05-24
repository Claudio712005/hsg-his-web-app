package br.com.hsg.domain.entity;

import br.com.hsg.domain.converter.IndicativoStatusConverter;
import br.com.hsg.domain.enums.IndicativoStatus;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_CONV", schema = "hsg")
public class Convenio {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CONV")
    @SequenceGenerator(name = "SEQ_CONV", sequenceName = "SEQ_CONV", allocationSize = 1)
    @Column(name = "ID_CONV")
    private Long id;

    @Getter
    @Column(name = "NM_CONV", length = 150, nullable = false, unique = true)
    private String nome;

    @Getter
    @Column(name = "DS_CONV", length = 500)
    private String descricao;

    @Getter
    @Column(name = "NR_REG_ANS", length = 20)
    private String registroAns;

    @Getter
    @Column(name = "NR_CNPJ", length = 20)
    private String cnpj;

    @Getter
    @Column(name = "DS_SITE", length = 200)
    private String site;

    @Getter
    @Column(name = "NR_TEL_CONV", length = 20)
    private String telefone;

    @Getter
    @Convert(converter = IndicativoStatusConverter.class)
    @Column(name = "ST_CONV", nullable = false, length = 1)
    private IndicativoStatus status;

    @Getter
    @Column(name = "DT_CAD_CONV", nullable = false)
    private LocalDateTime dataCadastro;

    @Getter
    @Column(name = "DT_ULT_ATU_CONV")
    private LocalDateTime dataUltimaAtualizacao;

    protected Convenio() {}

    public static Convenio criar(String nome, String descricao, String registroAns,
                                 String cnpj, String site, String telefone) {
        validar(nome);
        Convenio c = new Convenio();
        c.nome         = nome.trim();
        c.descricao    = descricao;
        c.registroAns  = registroAns;
        c.cnpj         = cnpj;
        c.site         = site;
        c.telefone     = telefone;
        c.status       = IndicativoStatus.A;
        c.dataCadastro = LocalDateTime.now();
        return c;
    }

    public static void validar(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do convênio é obrigatório.");
        }
        if (nome.trim().length() > 150) {
            throw new IllegalArgumentException("O nome do convênio não pode exceder 150 caracteres.");
        }
    }

    public void atualizar(String nome, String descricao, String registroAns,
                          String cnpj, String site, String telefone) {
        validar(nome);
        this.nome         = nome.trim();
        this.descricao    = descricao;
        this.registroAns  = registroAns;
        this.cnpj         = cnpj;
        this.site         = site;
        this.telefone     = telefone;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public void ativar() {
        this.status = IndicativoStatus.A;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public void inativar() {
        this.status = IndicativoStatus.I;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public boolean isAtivo() {
        return IndicativoStatus.A.equals(status);
    }
}
