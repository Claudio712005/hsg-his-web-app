package br.com.hsg.web.dto.form;

import lombok.Data;

import java.io.Serializable;

@Data
public class AtualizacaoClinicaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Double peso;
    private Double altura;
    private String tipoSanguineo;
    private String motivo;
}
