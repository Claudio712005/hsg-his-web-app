package br.com.hsg.web.dto.response;

import lombok.Data;

@Data
public class HistoricoAlergiaDTO {

    private Long   id;
    private Long   pacienteId;
    private String nomePaciente;
    private String nomeAlergia;
    private String descricao;
    private String tipoDescricao;
    private String gravidadeDescricao;
    private String gravidadeCssClass;
    private String reacao;
    private String dataUltimaReacao;
    private String observacao;
    private String observacaoAvaliador;
    private String statusDescricao;
    private String statusCssClass;
    private String dataCadastro;
    private String dataAvaliacao;
}
