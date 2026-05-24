package br.com.hsg.web.model;

import br.com.hsg.domain.entity.Convenio;
import br.com.hsg.service.facade.admin.ConvenioServiceFacade;
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
public class ConvenioLazyModelTest {

    @Mock private ConvenioServiceFacade service;

    @Test
    public void load_deveDefinirRowCountERetornarPagina() {
        ConvenioLazyModel model = new ConvenioLazyModel(service);
        model.setFiltroNome("Uni");
        model.setFiltroStatus("A");
        when(service.contarConvenios("Uni", "A")).thenReturn(2L);
        when(service.listarConveniosPaginado(0, 10, "Uni", "A", "nome", true))
                .thenReturn(Arrays.asList(mock(Convenio.class), mock(Convenio.class)));

        int retornados = model.load(0, 10, "nome", SortOrder.ASCENDING, null).size();

        assertEquals(2, retornados);
        assertEquals(2, model.getRowCount());
    }

    @Test
    public void load_deveRetornarVazioQuandoTotalZero() {
        ConvenioLazyModel model = new ConvenioLazyModel(service);
        when(service.contarConvenios(null, null)).thenReturn(0L);

        assertTrue(model.load(0, 10, null, SortOrder.ASCENDING, null).isEmpty());
        verify(service, never()).listarConveniosPaginado(anyInt(), anyInt(), any(), any(), any(), anyBoolean());
    }

    @Test
    public void load_deveUsarDescendenteQuandoSortOrderDescending() {
        ConvenioLazyModel model = new ConvenioLazyModel(service);
        when(service.contarConvenios(null, null)).thenReturn(1L);
        when(service.listarConveniosPaginado(0, 10, null, null, "nome", false))
                .thenReturn(Arrays.asList(mock(Convenio.class)));

        model.load(0, 10, "nome", SortOrder.DESCENDING, null);

        verify(service).listarConveniosPaginado(0, 10, null, null, "nome", false);
    }
}
