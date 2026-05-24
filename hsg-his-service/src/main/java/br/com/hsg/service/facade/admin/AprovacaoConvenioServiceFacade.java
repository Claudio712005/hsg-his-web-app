package br.com.hsg.service.facade.admin;

import br.com.hsg.domain.entity.RegraCobertura;
import br.com.hsg.domain.entity.SolicitacaoConvenio;

import javax.ejb.Local;
import java.util.List;

@Local
public interface AprovacaoConvenioServiceFacade {

    SolicitacaoConvenio buscarPorId(Long id);

    List<RegraCobertura> listarRegrasDoPlano(Long idPlano);

    List<SolicitacaoConvenio> listarPaginado(int primeiro, int tamanho,
                                             String filtroPaciente, String filtroStatus,
                                             String campoOrdenacao, boolean crescente);

    long contarTotal(String filtroPaciente, String filtroStatus);

    long contarPendentes();

    void aprovar(Long idSolicitacao, Long idAprovador);

    void rejeitar(Long idSolicitacao, Long idAprovador, String motivoRejeicao);
}
