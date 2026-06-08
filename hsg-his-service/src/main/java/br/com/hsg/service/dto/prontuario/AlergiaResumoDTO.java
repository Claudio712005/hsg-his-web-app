package br.com.hsg.service.dto.prontuario;

import java.io.Serializable;

public class AlergiaResumoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long   id;
    private String nome;
    private String tipo;
    private String gravidade;
    private String status;
    private String reacao;

    public Long getId()                  { return id; }
    public void setId(Long v)            { this.id = v; }
    public String getNome()              { return nome; }
    public void setNome(String v)        { this.nome = v; }
    public String getTipo()              { return tipo; }
    public void setTipo(String v)        { this.tipo = v; }
    public String getGravidade()         { return gravidade; }
    public void setGravidade(String v)   { this.gravidade = v; }
    public String getStatus()            { return status; }
    public void setStatus(String v)      { this.status = v; }
    public String getReacao()            { return reacao; }
    public void setReacao(String v)      { this.reacao = v; }
}
