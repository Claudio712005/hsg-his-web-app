package br.com.hsg.web.dto.response;

import br.com.hsg.domain.enums.TipoSanguineoEnum;
import lombok.Data;

@Data
public class PacienteResponseDTO {

    private Long id;
    private String nomeCompleto;
    private String email;
    private String username;
    private String telefone;

    private Long altura;
    private Long peso;

    private TipoSanguineoEnum tipoSanguineo;
}
