package br.com.hsg.web.dto.form;

import lombok.Data;

import java.io.Serializable;

@Data
public class PacienteAtualizacaoFormDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String primeiroNome;
    private String sobrenome;
    private String email;
    private String telefone;
    private String motivo;
}
