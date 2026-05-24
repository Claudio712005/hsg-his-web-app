package br.com.hsg.service.facade.admin;

import br.com.hsg.domain.entity.Convenio;
import br.com.hsg.domain.entity.PlanoConvenio;
import br.com.hsg.domain.entity.RegraCobertura;
import br.com.hsg.domain.enums.TipoCoberturaPlano;

import javax.ejb.Local;
import java.math.BigDecimal;
import java.util.List;

@Local
public interface ConvenioServiceFacade {

    Convenio buscarConvenioPorId(Long id);

    List<Convenio> listarConveniosAtivos();

    List<Convenio> listarConveniosPaginado(int primeiro, int tamanho,
                                           String filtroNome, String filtroStatus,
                                           String campoOrdenacao, boolean crescente);

    long contarConvenios(String filtroNome, String filtroStatus);

    long contarConveniosAtivos();

    Convenio criarConvenio(String nome, String descricao, String registroAns,
                            String cnpj, String site, String telefone);

    Convenio atualizarConvenio(Long id, String nome, String descricao, String registroAns,
                                String cnpj, String site, String telefone);

    void ativarConvenio(Long id);

    void inativarConvenio(Long id);

    PlanoConvenio buscarPlanoPorId(Long id);

    List<PlanoConvenio> listarPlanosAtivosPorConvenio(Long idConvenio);

    List<PlanoConvenio> listarPlanosPaginado(int primeiro, int tamanho,
                                              Long filtroConvenioId, String filtroNome,
                                              String filtroCobertura, String filtroStatus,
                                              String campoOrdenacao, boolean crescente);

    long contarPlanos(Long filtroConvenioId, String filtroNome,
                       String filtroCobertura, String filtroStatus);

    long contarPlanosAtivos();

    PlanoConvenio criarPlano(Long idConvenio, String nome, String codigo, String descricao,
                              TipoCoberturaPlano tipoCobertura, BigDecimal valorMensalidade,
                              boolean acomodacaoIndividual);

    PlanoConvenio atualizarPlano(Long id, String nome, String codigo, String descricao,
                                  TipoCoberturaPlano tipoCobertura, BigDecimal valorMensalidade,
                                  boolean acomodacaoIndividual);

    void ativarPlano(Long id);

    void inativarPlano(Long id);

    RegraCobertura buscarRegraPorId(Long id);

    List<RegraCobertura> listarRegrasAtivasPorPlano(Long idPlano);

    List<RegraCobertura> listarRegrasPaginado(int primeiro, int tamanho,
                                               Long filtroPlanoId, String filtroProcedimento,
                                               String filtroCategoria, String filtroStatus,
                                               String campoOrdenacao, boolean crescente);

    long contarRegras(Long filtroPlanoId, String filtroProcedimento,
                       String filtroCategoria, String filtroStatus);

    RegraCobertura criarRegra(Long idPlano, String procedimento, String categoria,
                               Integer carenciaDias, BigDecimal percentualCopagamento,
                               boolean cobertura, String observacao);

    RegraCobertura atualizarRegra(Long id, String procedimento, String categoria,
                                   Integer carenciaDias, BigDecimal percentualCopagamento,
                                   boolean cobertura, String observacao);

    void ativarRegra(Long id);

    void inativarRegra(Long id);
}
