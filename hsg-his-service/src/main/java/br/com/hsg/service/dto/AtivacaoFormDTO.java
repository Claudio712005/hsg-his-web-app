package br.com.hsg.service.dto;

import java.io.Serializable;
import java.util.Date;

public class AtivacaoFormDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date   dataNascimento;
    private String telefone;
    private String senha;
    private String confirmacaoSenha;

    private Long   especialidadeId;

    private String especialidadeEnf;
    private String setor;

    public Date   getDataNascimento()                     { return dataNascimento; }
    public void   setDataNascimento(Date dataNascimento)  { this.dataNascimento = dataNascimento; }

    public String getTelefone()                           { return telefone; }
    public void   setTelefone(String telefone)            { this.telefone = telefone; }

    public String getSenha()                              { return senha; }
    public void   setSenha(String senha)                  { this.senha = senha; }

    public String getConfirmacaoSenha()                            { return confirmacaoSenha; }
    public void   setConfirmacaoSenha(String confirmacaoSenha)     { this.confirmacaoSenha = confirmacaoSenha; }

    public Long   getEspecialidadeId()                    { return especialidadeId; }
    public void   setEspecialidadeId(Long especialidadeId){ this.especialidadeId = especialidadeId; }

    public String getEspecialidadeEnf()                            { return especialidadeEnf; }
    public void   setEspecialidadeEnf(String especialidadeEnf)     { this.especialidadeEnf = especialidadeEnf; }

    public String getSetor()                              { return setor; }
    public void   setSetor(String setor)                  { this.setor = setor; }
}
