package br.com.hsg.web.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class AlergiaResponseDTO {

    private Long   id;
    private String nome;
    private String descricao;
    private String tipoDescricao;
    private String gravidadeDescricao;
    private String statusDescricao;
    private String statusCssClass;
    private String reacao;
    private String observacao;
    private String dataUltimaReacao;
    private String dataCadastro;
    private boolean excluivel;

    private List<AlergiaHistoricoDTO> historico;

    @Data
    public static class AlergiaHistoricoDTO {
        private String acaoDescricao;
        private String nomeSnap;
        private String tipoSnap;
        private String gravidadeSnap;
        private String statusSnap;
        private String dataAcao;
    }
}
