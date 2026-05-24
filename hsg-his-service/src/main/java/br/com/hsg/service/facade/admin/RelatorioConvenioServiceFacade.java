package br.com.hsg.service.facade.admin;

import br.com.hsg.domain.entity.PacienteConvenio;
import br.com.hsg.domain.enums.TipoCoberturaPlano;

import javax.ejb.Local;
import java.math.BigDecimal;
import java.util.List;

@Local
public interface RelatorioConvenioServiceFacade {

    List<Object[]> contarVinculosPorConvenio();

    List<Object[]> contarVinculosPorConvenio(TipoCoberturaPlano filtroCobertura);

    List<Object[]> contarVinculosPorPlano(Long idConvenio);

    List<Object[]> contarTopPlanos(int limite);

    BigDecimal somaReceitaMensalAtiva();

    long contarTotalVinculosAtivos();

    long contarSolicitacoesPendentes();

    List<PacienteConvenio> listarVinculosAtivosPaginado(int primeiro, int tamanho,
                                                        String filtroPaciente, Long filtroConvenioId,
                                                        String campoOrdenacao, boolean crescente);

    long contarVinculosAtivos(String filtroPaciente, Long filtroConvenioId);
}
