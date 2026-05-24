package br.com.hsg.service.impl.admin;

import br.com.hsg.dao.ConvenioDAO;
import br.com.hsg.dao.PlanoConvenioDAO;
import br.com.hsg.dao.RegraCoberturaDAO;
import br.com.hsg.domain.entity.Convenio;
import br.com.hsg.domain.entity.PlanoConvenio;
import br.com.hsg.domain.entity.RegraCobertura;
import br.com.hsg.domain.enums.TipoCoberturaPlano;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConvenioServiceImplTest {

    @Mock private ConvenioDAO convenioDAO;
    @Mock private PlanoConvenioDAO planoConvenioDAO;
    @Mock private RegraCoberturaDAO regraCoberturaDAO;

    @InjectMocks private ConvenioServiceImpl service;

    @Test
    public void criarConvenio_deveSalvarQuandoNomeUnico() {
        Convenio salvo = mock(Convenio.class);
        when(convenioDAO.buscarPorNome("Unimed")).thenReturn(null);
        when(convenioDAO.salvar(any(Convenio.class))).thenReturn(salvo);

        Convenio resultado = service.criarConvenio("Unimed", null, null, null, null, null);

        assertSame(salvo, resultado);
        verify(convenioDAO).salvar(any(Convenio.class));
    }

    @Test(expected = IllegalStateException.class)
    public void criarConvenio_deveLancarExcecaoSeNomeDuplicado() {
        when(convenioDAO.buscarPorNome("Unimed")).thenReturn(mock(Convenio.class));
        service.criarConvenio("Unimed", null, null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void atualizarConvenio_deveLancarExcecaoSeNaoEncontrado() {
        when(convenioDAO.buscarPorId(9L)).thenReturn(null);
        service.atualizarConvenio(9L, "Nome", null, null, null, null, null);
    }

    @Test(expected = IllegalStateException.class)
    public void atualizarConvenio_deveLancarExcecaoSeNomeUsadoPorOutro() {
        Convenio atual = mock(Convenio.class);
        Convenio outro = mock(Convenio.class);
        when(convenioDAO.buscarPorId(1L)).thenReturn(atual);
        when(convenioDAO.buscarPorNome("Bradesco")).thenReturn(outro);
        when(outro.getId()).thenReturn(2L);
        service.atualizarConvenio(1L, "Bradesco", null, null, null, null, null);
    }

    @Test
    public void inativarConvenio_deveInativarQuandoSemPlanosAtivos() {
        Convenio c = mock(Convenio.class);
        when(convenioDAO.buscarPorId(1L)).thenReturn(c);
        when(planoConvenioDAO.contarTotal(1L, null, null, "A")).thenReturn(0L);

        service.inativarConvenio(1L);

        verify(c).inativar();
        verify(convenioDAO).atualizar(c);
    }

    @Test(expected = IllegalStateException.class)
    public void inativarConvenio_deveLancarExcecaoSeExistemPlanosAtivos() {
        Convenio c = mock(Convenio.class);
        when(convenioDAO.buscarPorId(1L)).thenReturn(c);
        when(planoConvenioDAO.contarTotal(1L, null, null, "A")).thenReturn(3L);
        service.inativarConvenio(1L);
    }

    @Test
    public void criarPlano_deveSalvarQuandoConvenioAtivoENomeUnico() {
        Convenio convenio = mock(Convenio.class);
        PlanoConvenio salvo = mock(PlanoConvenio.class);
        when(convenioDAO.buscarPorId(1L)).thenReturn(convenio);
        when(convenio.isAtivo()).thenReturn(true);
        when(planoConvenioDAO.buscarPorNomeEConvenio(1L, "Plano A")).thenReturn(null);
        when(planoConvenioDAO.salvar(any(PlanoConvenio.class))).thenReturn(salvo);

        PlanoConvenio resultado = service.criarPlano(1L, "Plano A", null, null,
                TipoCoberturaPlano.COMPLETO, BigDecimal.TEN, false);

        assertSame(salvo, resultado);
        verify(planoConvenioDAO).salvar(any(PlanoConvenio.class));
    }

    @Test(expected = IllegalStateException.class)
    public void criarPlano_deveLancarExcecaoSeConvenioInativo() {
        Convenio convenio = mock(Convenio.class);
        when(convenioDAO.buscarPorId(1L)).thenReturn(convenio);
        when(convenio.isAtivo()).thenReturn(false);
        service.criarPlano(1L, "Plano A", null, null, TipoCoberturaPlano.COMPLETO, BigDecimal.TEN, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criarPlano_deveLancarExcecaoSeConvenioNaoEncontrado() {
        when(convenioDAO.buscarPorId(1L)).thenReturn(null);
        service.criarPlano(1L, "Plano A", null, null, TipoCoberturaPlano.COMPLETO, BigDecimal.TEN, false);
    }

    @Test(expected = IllegalStateException.class)
    public void ativarPlano_deveLancarExcecaoSeConvenioInativo() {
        PlanoConvenio p = mock(PlanoConvenio.class);
        Convenio convenio = mock(Convenio.class);
        when(planoConvenioDAO.buscarPorId(1L)).thenReturn(p);
        when(p.getConvenio()).thenReturn(convenio);
        when(convenio.isAtivo()).thenReturn(false);
        service.ativarPlano(1L);
    }

    @Test
    public void criarRegra_deveSalvarQuandoPlanoAtivoEProcedimentoUnico() {
        PlanoConvenio plano = mock(PlanoConvenio.class);
        RegraCobertura salva = mock(RegraCobertura.class);
        when(planoConvenioDAO.buscarPorId(1L)).thenReturn(plano);
        when(plano.isAtivo()).thenReturn(true);
        when(regraCoberturaDAO.buscarPorProcedimentoEPlano(1L, "Consulta")).thenReturn(null);
        when(regraCoberturaDAO.salvar(any(RegraCobertura.class))).thenReturn(salva);

        RegraCobertura resultado = service.criarRegra(1L, "Consulta", null, 0,
                BigDecimal.ZERO, true, null);

        assertSame(salva, resultado);
        verify(regraCoberturaDAO).salvar(any(RegraCobertura.class));
    }

    @Test(expected = IllegalStateException.class)
    public void criarRegra_deveLancarExcecaoSePlanoInativo() {
        PlanoConvenio plano = mock(PlanoConvenio.class);
        when(planoConvenioDAO.buscarPorId(1L)).thenReturn(plano);
        when(plano.isAtivo()).thenReturn(false);
        service.criarRegra(1L, "Consulta", null, 0, BigDecimal.ZERO, true, null);
    }

    @Test(expected = IllegalStateException.class)
    public void criarRegra_deveLancarExcecaoSeProcedimentoDuplicado() {
        PlanoConvenio plano = mock(PlanoConvenio.class);
        when(planoConvenioDAO.buscarPorId(1L)).thenReturn(plano);
        when(plano.isAtivo()).thenReturn(true);
        when(regraCoberturaDAO.buscarPorProcedimentoEPlano(1L, "Consulta")).thenReturn(mock(RegraCobertura.class));
        service.criarRegra(1L, "Consulta", null, 0, BigDecimal.ZERO, true, null);
    }
}
