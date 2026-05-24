package br.com.hsg.web.model;

import br.com.hsg.domain.entity.SolicitacaoConvenio;
import br.com.hsg.service.facade.admin.AprovacaoConvenioServiceFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.primefaces.model.SortOrder;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SolicitacaoConvenioLazyModelTest {

    @Mock private AprovacaoConvenioServiceFacade service;

    @Test
    public void aplicarFiltros_deveArmazenarFiltros() {
        SolicitacaoConvenioLazyModel model = new SolicitacaoConvenioLazyModel(service);
        model.aplicarFiltros("Joao", "P");
        assertEquals("Joao", model.getFiltroPaciente());
        assertEquals("P", model.getFiltroStatus());
    }

    @Test
    public void load_deveDefinirRowCountERetornarPagina() {
        SolicitacaoConvenioLazyModel model = new SolicitacaoConvenioLazyModel(service);
        model.aplicarFiltros("Joao", "P");
        when(service.contarTotal("Joao", "P")).thenReturn(1L);
        when(service.listarPaginado(0, 10, "Joao", "P", "dataCadastro", true))
                .thenReturn(Arrays.asList(mock(SolicitacaoConvenio.class)));

        int retornados = model.load(0, 10, "dataCadastro", SortOrder.ASCENDING, null).size();

        assertEquals(1, retornados);
        assertEquals(1, model.getRowCount());
    }

    @Test
    public void load_deveRetornarVazioQuandoTotalZero() {
        SolicitacaoConvenioLazyModel model = new SolicitacaoConvenioLazyModel(service);
        when(service.contarTotal(null, null)).thenReturn(0L);

        assertTrue(model.load(0, 10, null, SortOrder.ASCENDING, null).isEmpty());
        verify(service, never()).listarPaginado(anyInt(), anyInt(), any(), any(), any(), anyBoolean());
    }
}
