package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.StatusSlotAgenda;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class AgendaMedicaSlotTest {

    private Medico medico;
    private LocalDateTime ini;
    private LocalDateTime fim;

    @Before
    public void setUp() {
        this.medico = mock(Medico.class);
        this.ini    = LocalDateTime.of(2026, 6, 1, 8, 0);
        this.fim    = LocalDateTime.of(2026, 6, 1, 8, 30);
    }

    @Test
    public void criar_deveIniciarComoLivre() {
        AgendaMedicaSlot s = AgendaMedicaSlot.criar(medico, null, ini, fim);

        assertEquals(StatusSlotAgenda.LIVRE, s.getStatus());
        assertTrue(s.isLivre());
        assertNotNull(s.getDataCadastro());
        assertNull(s.getIdConsulta());
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarQuandoMedicoNulo() {
        AgendaMedicaSlot.criar(null, null, ini, fim);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarQuandoFimNaoMaiorQueInicio() {
        AgendaMedicaSlot.criar(medico, null, ini, ini);
    }

    @Test
    public void reservar_deveTransicionarLivreParaReservadoEGuardarConsulta() {
        AgendaMedicaSlot s = AgendaMedicaSlot.criar(medico, null, ini, fim);

        s.reservar(99L);

        assertEquals(StatusSlotAgenda.RESERVADO, s.getStatus());
        assertEquals(Long.valueOf(99L), s.getIdConsulta());
        assertNotNull(s.getDataUltimaAtualizacao());
    }

    @Test(expected = IllegalStateException.class)
    public void reservar_deveLancarQuandoSlotNaoEstaLivre() {
        AgendaMedicaSlot s = AgendaMedicaSlot.criar(medico, null, ini, fim);
        s.reservar(1L);
        s.reservar(2L);
    }

    @Test
    public void liberar_deveVoltarParaLivreELimparConsulta() {
        AgendaMedicaSlot s = AgendaMedicaSlot.criar(medico, null, ini, fim);
        s.reservar(10L);

        s.liberar();

        assertEquals(StatusSlotAgenda.LIVRE, s.getStatus());
        assertNull(s.getIdConsulta());
    }

    @Test(expected = IllegalStateException.class)
    public void bloquear_deveLancarQuandoSlotReservado() {
        AgendaMedicaSlot s = AgendaMedicaSlot.criar(medico, null, ini, fim);
        s.reservar(1L);
        s.bloquear();
    }

    @Test
    public void bloquear_deveTransicionarLivreParaBloqueado() {
        AgendaMedicaSlot s = AgendaMedicaSlot.criar(medico, null, ini, fim);

        s.bloquear();

        assertEquals(StatusSlotAgenda.BLOQUEADO, s.getStatus());
    }

    @Test
    public void cancelar_deveAtualizarStatus() {
        AgendaMedicaSlot s = AgendaMedicaSlot.criar(medico, null, ini, fim);

        s.cancelar();

        assertEquals(StatusSlotAgenda.CANCELADO, s.getStatus());
    }
}
