package br.com.hsg.service.impl.admin;

import br.com.hsg.dao.ConvenioDAO;
import br.com.hsg.dao.PlanoConvenioDAO;
import br.com.hsg.dao.RegraCoberturaDAO;
import br.com.hsg.domain.entity.Convenio;
import br.com.hsg.domain.entity.PlanoConvenio;
import br.com.hsg.domain.entity.RegraCobertura;
import br.com.hsg.domain.enums.TipoCoberturaPlano;
import br.com.hsg.service.facade.admin.ConvenioServiceFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class ConvenioServiceImpl implements ConvenioServiceFacade {

    private static final Logger LOG = Logger.getLogger(ConvenioServiceImpl.class.getName());

    @EJB private ConvenioDAO convenioDAO;
    @EJB private PlanoConvenioDAO planoConvenioDAO;
    @EJB private RegraCoberturaDAO regraCoberturaDAO;

    @Override
    public Convenio buscarConvenioPorId(Long id) {
        return convenioDAO.buscarPorId(id);
    }

    @Override
    public List<Convenio> listarConveniosAtivos() {
        return convenioDAO.listarAtivos();
    }

    @Override
    public List<Convenio> listarConveniosPaginado(int primeiro, int tamanho,
                                                   String filtroNome, String filtroStatus,
                                                   String campoOrdenacao, boolean crescente) {
        return convenioDAO.listarPaginado(primeiro, tamanho, filtroNome, filtroStatus,
                campoOrdenacao, crescente);
    }

    @Override
    public long contarConvenios(String filtroNome, String filtroStatus) {
        return convenioDAO.contarTotal(filtroNome, filtroStatus);
    }

    @Override
    public long contarConveniosAtivos() {
        return convenioDAO.contarAtivos();
    }

    @Override
    public Convenio criarConvenio(String nome, String descricao, String registroAns,
                                   String cnpj, String site, String telefone) {
        Convenio existente = convenioDAO.buscarPorNome(nome);
        if (existente != null) {
            throw new IllegalStateException("Já existe um convênio cadastrado com este nome.");
        }
        Convenio c = Convenio.criar(nome, descricao, registroAns, cnpj, site, telefone);
        Convenio salvo = convenioDAO.salvar(c);
        LOG.info("[ConvenioServiceImpl] Convênio criado: id=" + salvo.getId() + ", nome=" + salvo.getNome());
        return salvo;
    }

    @Override
    public Convenio atualizarConvenio(Long id, String nome, String descricao, String registroAns,
                                       String cnpj, String site, String telefone) {
        Convenio c = convenioDAO.buscarPorId(id);
        if (c == null) {
            throw new IllegalArgumentException("Convênio não encontrado.");
        }
        Convenio existente = convenioDAO.buscarPorNome(nome);
        if (existente != null && !existente.getId().equals(id)) {
            throw new IllegalStateException("Já existe outro convênio cadastrado com este nome.");
        }
        c.atualizar(nome, descricao, registroAns, cnpj, site, telefone);
        Convenio atualizado = convenioDAO.atualizar(c);
        LOG.info("[ConvenioServiceImpl] Convênio atualizado: id=" + id);
        return atualizado;
    }

    @Override
    public void ativarConvenio(Long id) {
        Convenio c = convenioDAO.buscarPorId(id);
        if (c == null) {
            throw new IllegalArgumentException("Convênio não encontrado.");
        }
        c.ativar();
        convenioDAO.atualizar(c);
    }

    @Override
    public void inativarConvenio(Long id) {
        Convenio c = convenioDAO.buscarPorId(id);
        if (c == null) {
            throw new IllegalArgumentException("Convênio não encontrado.");
        }
        long planosAtivos = planoConvenioDAO.contarTotal(id, null, null, "A");
        if (planosAtivos > 0) {
            throw new IllegalStateException(
                "Não é possível inativar o convênio: existem " + planosAtivos +
                " plano(s) ativo(s) vinculado(s). Inative os planos primeiro.");
        }
        c.inativar();
        convenioDAO.atualizar(c);
    }

    @Override
    public PlanoConvenio buscarPlanoPorId(Long id) {
        return planoConvenioDAO.buscarPorId(id);
    }

    @Override
    public List<PlanoConvenio> listarPlanosAtivosPorConvenio(Long idConvenio) {
        return planoConvenioDAO.listarAtivosPorConvenio(idConvenio);
    }

    @Override
    public List<PlanoConvenio> listarPlanosPaginado(int primeiro, int tamanho,
                                                     Long filtroConvenioId, String filtroNome,
                                                     String filtroCobertura, String filtroStatus,
                                                     String campoOrdenacao, boolean crescente) {
        return planoConvenioDAO.listarPaginado(primeiro, tamanho, filtroConvenioId, filtroNome,
                filtroCobertura, filtroStatus, campoOrdenacao, crescente);
    }

    @Override
    public long contarPlanos(Long filtroConvenioId, String filtroNome,
                              String filtroCobertura, String filtroStatus) {
        return planoConvenioDAO.contarTotal(filtroConvenioId, filtroNome, filtroCobertura, filtroStatus);
    }

    @Override
    public long contarPlanosAtivos() {
        return planoConvenioDAO.contarAtivos();
    }

    @Override
    public PlanoConvenio criarPlano(Long idConvenio, String nome, String codigo, String descricao,
                                     TipoCoberturaPlano tipoCobertura, BigDecimal valorMensalidade,
                                     boolean acomodacaoIndividual) {
        Convenio convenio = convenioDAO.buscarPorId(idConvenio);
        if (convenio == null) {
            throw new IllegalArgumentException("Convênio não encontrado.");
        }
        if (!convenio.isAtivo()) {
            throw new IllegalStateException("Não é possível criar plano em convênio inativo.");
        }
        PlanoConvenio existente = planoConvenioDAO.buscarPorNomeEConvenio(idConvenio, nome);
        if (existente != null) {
            throw new IllegalStateException("Já existe um plano com este nome neste convênio.");
        }
        PlanoConvenio p = PlanoConvenio.criar(convenio, nome, codigo, descricao,
                tipoCobertura, valorMensalidade, acomodacaoIndividual);
        PlanoConvenio salvo = planoConvenioDAO.salvar(p);
        LOG.info("[ConvenioServiceImpl] Plano criado: id=" + salvo.getId() +
                ", convenio=" + idConvenio + ", nome=" + salvo.getNome());
        return salvo;
    }

    @Override
    public PlanoConvenio atualizarPlano(Long id, String nome, String codigo, String descricao,
                                         TipoCoberturaPlano tipoCobertura, BigDecimal valorMensalidade,
                                         boolean acomodacaoIndividual) {
        PlanoConvenio p = planoConvenioDAO.buscarPorId(id);
        if (p == null) {
            throw new IllegalArgumentException("Plano não encontrado.");
        }
        PlanoConvenio existente = planoConvenioDAO.buscarPorNomeEConvenio(p.getConvenio().getId(), nome);
        if (existente != null && !existente.getId().equals(id)) {
            throw new IllegalStateException("Já existe outro plano com este nome neste convênio.");
        }
        p.atualizar(nome, codigo, descricao, tipoCobertura, valorMensalidade, acomodacaoIndividual);
        PlanoConvenio atualizado = planoConvenioDAO.atualizar(p);
        LOG.info("[ConvenioServiceImpl] Plano atualizado: id=" + id);
        return atualizado;
    }

    @Override
    public void ativarPlano(Long id) {
        PlanoConvenio p = planoConvenioDAO.buscarPorId(id);
        if (p == null) {
            throw new IllegalArgumentException("Plano não encontrado.");
        }
        if (!p.getConvenio().isAtivo()) {
            throw new IllegalStateException("Não é possível ativar plano: o convênio está inativo.");
        }
        p.ativar();
        planoConvenioDAO.atualizar(p);
    }

    @Override
    public void inativarPlano(Long id) {
        PlanoConvenio p = planoConvenioDAO.buscarPorId(id);
        if (p == null) {
            throw new IllegalArgumentException("Plano não encontrado.");
        }
        p.inativar();
        planoConvenioDAO.atualizar(p);
    }

    @Override
    public RegraCobertura buscarRegraPorId(Long id) {
        return regraCoberturaDAO.buscarPorId(id);
    }

    @Override
    public List<RegraCobertura> listarRegrasAtivasPorPlano(Long idPlano) {
        return regraCoberturaDAO.listarAtivasPorPlano(idPlano);
    }

    @Override
    public List<RegraCobertura> listarRegrasPaginado(int primeiro, int tamanho,
                                                      Long filtroPlanoId, String filtroProcedimento,
                                                      String filtroCategoria, String filtroStatus,
                                                      String campoOrdenacao, boolean crescente) {
        return regraCoberturaDAO.listarPaginado(primeiro, tamanho, filtroPlanoId, filtroProcedimento,
                filtroCategoria, filtroStatus, campoOrdenacao, crescente);
    }

    @Override
    public long contarRegras(Long filtroPlanoId, String filtroProcedimento,
                              String filtroCategoria, String filtroStatus) {
        return regraCoberturaDAO.contarTotal(filtroPlanoId, filtroProcedimento, filtroCategoria, filtroStatus);
    }

    @Override
    public RegraCobertura criarRegra(Long idPlano, String procedimento, String categoria,
                                      Integer carenciaDias, BigDecimal percentualCopagamento,
                                      boolean cobertura, String observacao) {
        PlanoConvenio plano = planoConvenioDAO.buscarPorId(idPlano);
        if (plano == null) {
            throw new IllegalArgumentException("Plano não encontrado.");
        }
        if (!plano.isAtivo()) {
            throw new IllegalStateException("Não é possível criar regra em plano inativo.");
        }
        RegraCobertura existente = regraCoberturaDAO.buscarPorProcedimentoEPlano(idPlano, procedimento);
        if (existente != null) {
            throw new IllegalStateException("Já existe uma regra para este procedimento neste plano.");
        }
        RegraCobertura r = RegraCobertura.criar(plano, procedimento, categoria,
                carenciaDias, percentualCopagamento, cobertura, observacao);
        RegraCobertura salva = regraCoberturaDAO.salvar(r);
        LOG.info("[ConvenioServiceImpl] Regra criada: id=" + salva.getId() +
                ", plano=" + idPlano + ", procedimento=" + salva.getProcedimento());
        return salva;
    }

    @Override
    public RegraCobertura atualizarRegra(Long id, String procedimento, String categoria,
                                          Integer carenciaDias, BigDecimal percentualCopagamento,
                                          boolean cobertura, String observacao) {
        RegraCobertura r = regraCoberturaDAO.buscarPorId(id);
        if (r == null) {
            throw new IllegalArgumentException("Regra não encontrada.");
        }
        RegraCobertura existente = regraCoberturaDAO.buscarPorProcedimentoEPlano(
                r.getPlano().getId(), procedimento);
        if (existente != null && !existente.getId().equals(id)) {
            throw new IllegalStateException("Já existe outra regra para este procedimento neste plano.");
        }
        r.atualizar(procedimento, categoria, carenciaDias, percentualCopagamento, cobertura, observacao);
        RegraCobertura atualizada = regraCoberturaDAO.atualizar(r);
        LOG.info("[ConvenioServiceImpl] Regra atualizada: id=" + id);
        return atualizada;
    }

    @Override
    public void ativarRegra(Long id) {
        RegraCobertura r = regraCoberturaDAO.buscarPorId(id);
        if (r == null) {
            throw new IllegalArgumentException("Regra não encontrada.");
        }
        r.ativar();
        regraCoberturaDAO.atualizar(r);
    }

    @Override
    public void inativarRegra(Long id) {
        RegraCobertura r = regraCoberturaDAO.buscarPorId(id);
        if (r == null) {
            throw new IllegalArgumentException("Regra não encontrada.");
        }
        r.inativar();
        regraCoberturaDAO.atualizar(r);
    }
}
