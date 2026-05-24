package br.com.hsg.web.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long   id;
    private String nomeCompleto;
    private String email;
    private String username;
}
