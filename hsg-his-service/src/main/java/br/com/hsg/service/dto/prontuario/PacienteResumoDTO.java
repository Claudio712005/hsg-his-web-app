package br.com.hsg.service.dto.prontuario;

import java.io.Serializable;
import java.time.LocalDate;

public class PacienteResumoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long   id;
    private String nomeCompleto;
    private LocalDate dataNascimento;
    private Integer idade;
    private String sexo;
    private String cpfMascarado;
    private String email;
    private String telefone;
    private String convenioPlano;
    private String convenioCarteirinhaMascarada;

    public Long getId()                                   { return id; }
    public void setId(Long v)                             { this.id = v; }
    public String getNomeCompleto()                       { return nomeCompleto; }
    public void setNomeCompleto(String v)                 { this.nomeCompleto = v; }
    public LocalDate getDataNascimento()                  { return dataNascimento; }
    public void setDataNascimento(LocalDate v)            { this.dataNascimento = v; }
    public Integer getIdade()                             { return idade; }
    public void setIdade(Integer v)                       { this.idade = v; }
    public String getSexo()                               { return sexo; }
    public void setSexo(String v)                         { this.sexo = v; }
    public String getCpfMascarado()                       { return cpfMascarado; }
    public void setCpfMascarado(String v)                 { this.cpfMascarado = v; }
    public String getEmail()                              { return email; }
    public void setEmail(String v)                        { this.email = v; }
    public String getTelefone()                           { return telefone; }
    public void setTelefone(String v)                     { this.telefone = v; }
    public String getConvenioPlano()                      { return convenioPlano; }
    public void setConvenioPlano(String v)                { this.convenioPlano = v; }
    public String getConvenioCarteirinhaMascarada()       { return convenioCarteirinhaMascarada; }
    public void setConvenioCarteirinhaMascarada(String v) { this.convenioCarteirinhaMascarada = v; }
}
