package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.AcaoAlergia;
import br.com.hsg.domain.enums.GravidadeAlergia;
import br.com.hsg.domain.enums.StatusAlergia;
import br.com.hsg.domain.enums.TipoAlergia;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AlergiaHistoricoTest {

    @Test
    public void registrar_deveCopiarSnapshotDaAlergia() {
        Alergia alergia = mock(Alergia.class);
        when(alergia.getNome()).thenReturn("Dipirona");
        when(alergia.getTipoAlergia()).thenReturn(TipoAlergia.M);
        when(alergia.getGravidadeAlergia()).thenReturn(GravidadeAlergia.G);
        when(alergia.getStatusAlergia()).thenReturn(StatusAlergia.APROVADA);

        AlergiaHistorico h = AlergiaHistorico.registrar(alergia, 7L, AcaoAlergia.CRIADA);

        assertSame(alergia, h.getAlergia());
        assertEquals(Long.valueOf(7L), h.getIdUsuario());
        assertEquals(AcaoAlergia.CRIADA, h.getAcao());
        assertEquals("Dipirona", h.getNomeSnap());
        assertEquals(TipoAlergia.M, h.getTipoSnap());
        assertEquals(GravidadeAlergia.G, h.getGravidadeSnap());
        assertEquals(StatusAlergia.APROVADA, h.getStatusSnap());
    }
}
