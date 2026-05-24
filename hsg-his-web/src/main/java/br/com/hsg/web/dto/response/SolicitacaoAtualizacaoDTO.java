package br.com.hsg.web.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class SolicitacaoAtualizacaoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long   id;
    private String tipo;
    private String dataCadastro;
    private String tipoDescricao;
    private String resumo;

    private String nomePropostoCompleto;
    private String emailProposto;
    private String telefoneProposto;
    private String snapshotNome;
    private String snapshotEmail;
    private String snapshotTelefone;

    private String logradouroProposto;
    private String numeroProposto;
    private String complementoProposto;
    private String bairroProposto;
    private String cidadeProposta;
    private String estadoProposto;
    private String cepProposto;
    private String snapshotLogradouro;
    private String snapshotNumero;
    private String snapshotComplemento;
    private String snapshotBairro;
    private String snapshotCidade;
    private String snapshotEstado;
    private String snapshotCep;

    private String pesoProposto;
    private String alturaProposta;
    private String tipoSanguineoProposto;
    private String snapshotPeso;
    private String snapshotAltura;
    private String snapshotTipoSanguineo;

    private String motivo;
    private String statusDescricao;
    private String statusCssClass;
    private String enfermeiro;

    private String motivoCancelamento;
    private String dataCancelamento;
    private String tipoCancelador;
    private boolean cancelavel;
}
