package br.com.hsg.service.dto.prontuario;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AnotacaoResumoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long          id;
    private String        titulo;
    private String        descricao;
    private LocalDateTime dataCriacao;
    private String        responsavelTipo;

    public Long getId()                          { return id; }
    public void setId(Long v)                    { this.id = v; }
    public String getTitulo()                    { return titulo; }
    public void setTitulo(String v)              { this.titulo = v; }
    public String getDescricao()                 { return descricao; }
    public void setDescricao(String v)           { this.descricao = v; }
    public LocalDateTime getDataCriacao()        { return dataCriacao; }
    public void setDataCriacao(LocalDateTime v)  { this.dataCriacao = v; }
    public String getResponsavelTipo()           { return responsavelTipo; }
    public void setResponsavelTipo(String v)     { this.responsavelTipo = v; }
}
