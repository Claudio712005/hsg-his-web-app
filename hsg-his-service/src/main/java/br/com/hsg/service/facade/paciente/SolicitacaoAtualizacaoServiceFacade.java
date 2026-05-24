package br.com.hsg.service.facade.paciente;

import br.com.hsg.domain.entity.Endereco;
import br.com.hsg.domain.entity.SolicitacaoAtualizacao;
import br.com.hsg.domain.enums.Estado;
import br.com.hsg.domain.enums.TipoCancelador;
import br.com.hsg.domain.enums.TipoSolicitacao;

import javax.ejb.Local;
import java.util.List;

@Local
public interface SolicitacaoAtualizacaoServiceFacade {

    void solicitarAtualizacaoCadastral(
            Long pacienteId,
            String primeiroNome, String sobrenome,
            String email, String telefone,
            String motivo
    );

    void solicitarAtualizacaoEndereco(
            Long pacienteId,
            String logradouro, String numero, String complemento,
            String bairro, String cidade, Estado estado, String cep,
            String motivo
    );

    void solicitarAtualizacaoClinica(
            Long pacienteId,
            Double peso, Double altura,
            String tipoSanguineo,
            String motivo
    );

    void cancelarSolicitacao(Long solicitacaoId, Long idCancelador,
            TipoCancelador tipoCancelador, String motivoCancelamento);

    List<SolicitacaoAtualizacao> consultarHistorico(
            Long pacienteId, int inicio, int tamanho, List<TipoSolicitacao> tipos);

    long contarHistorico(Long pacienteId, List<TipoSolicitacao> tipos);

    Endereco buscarEnderecoPorPaciente(Long pacienteId);
}
