package br.com.hsg.web.dto.response;

import lombok.Data;

@Data
public class UsuarioClinicaDTO {

    private Long   id;
    private String nomeCompleto;
    private String email;
    private String username;
    private String tipo;
    private String registro;
    private String especialidade;
}
