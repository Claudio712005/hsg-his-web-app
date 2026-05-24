package br.com.hsg.service.impl.admin;

import br.com.hsg.dao.PacienteConvenioDAO;
import br.com.hsg.dao.SolicitacaoConvenioDAO;
import br.com.hsg.domain.entity.PacienteConvenio;
import br.com.hsg.domain.enums.TipoCoberturaPlano;
import br.com.hsg.service.facade.admin.RelatorioConvenioServiceFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class RelatorioConvenioServiceImpl implements RelatorioConvenioServiceFacade {

    @EJB private PacienteConvenioDAO    pacienteConvenioDAO;
    @EJB private SolicitacaoConvenioDAO solicitacaoConvenioDAO;

    @Override
    public List<Object[]> contarVinculosPorConvenio() {
        return pacienteConvenioDAO.contarPorConvenio();
    }

    @Override
    public List<Object[]> contarVinculosPorConvenio(TipoCoberturaPlano filtroCobertura) {
        return pacienteConvenioDAO.contarPorConvenio(filtroCobertura);
    }

    @Override
    public List<Object[]> contarVinculosPorPlano(Long idConvenio) {
        return pacienteConvenioDAO.contarPorPlanoDoConvenio(idConvenio);
    }

    @Override
    public List<Object[]> contarTopPlanos(int limite) {
        return pacienteConvenioDAO.contarPorPlanoGlobal(limite);
    }

    @Override
    public BigDecimal somaReceitaMensalAtiva() {
        return pacienteConvenioDAO.somaReceitaMensalAtiva();
    }

    @Override
    public long contarTotalVinculosAtivos() {
        return pacienteConvenioDAO.contarVinculosAtivos(null, null);
    }

    @Override
    public long contarSolicitacoesPendentes() {
        return solicitacaoConvenioDAO.contarPendentes();
    }

    @Override
    public List<PacienteConvenio> listarVinculosAtivosPaginado(int primeiro, int tamanho,
                                                               String filtroPaciente, Long filtroConvenioId,
                                                               String campoOrdenacao, boolean crescente) {
        return pacienteConvenioDAO.listarVinculosAtivosPaginado(primeiro, tamanho, filtroPaciente,
                filtroConvenioId, campoOrdenacao, crescente);
    }

    @Override
    public long contarVinculosAtivos(String filtroPaciente, Long filtroConvenioId) {
        return pacienteConvenioDAO.contarVinculosAtivos(filtroPaciente, filtroConvenioId);
    }
}
