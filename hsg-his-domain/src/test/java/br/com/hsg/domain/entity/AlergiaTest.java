package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.GravidadeAlergia;
import br.com.hsg.domain.enums.StatusAlergia;
import br.com.hsg.domain.enums.TipoAlergia;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class AlergiaTest {

    private Paciente paciente;

    @Before
    public void setUp() {
        paciente = mock(Paciente.class);
    }

    private Alergia alergiaInformada() {
        return Alergia.informar(
                paciente, 1L,
                "Dipirona", "Reação a dipirona",
                TipoAlergia.M, GravidadeAlergia.G,
                "Urticária", null, "Observação do paciente");
    }

    @Test
    public void informar_deveDefinirStatusInformadaEDataCadastro() {
        Alergia a = alergiaInformada();

        assertEquals("Dipirona", a.getNome());
        assertEquals(TipoAlergia.M, a.getTipoAlergia());
        assertEquals(GravidadeAlergia.G, a.getGravidadeAlergia());
        assertEquals(StatusAlergia.INFORMADA, a.getStatusAlergia());
        assertNotNull(a.getDataCadastro());
        assertNotNull(a.getDataUltimaAlteracao());
    }

    @Test
    public void aprovar_deveAlterarStatusParaAprovadaERegistrarAprovador() {
        Alergia a = alergiaInformada();

        a.aprovar(99L, "Confirmado pelo enfermeiro");

        assertEquals(StatusAlergia.APROVADA, a.getStatusAlergia());
        assertEquals(Long.valueOf(99L), a.getIdAprovador());
        assertEquals("Confirmado pelo enfermeiro", a.getObservacaoEnfermeiro());
        assertNotNull(a.getDataUltimaAlteracao());
    }

    @Test
    public void rejeitar_deveAlterarStatusParaRejeitadaERegistrarAprovador() {
        Alergia a = alergiaInformada();

        a.rejeitar(99L, "Duplicada no sistema");

        assertEquals(StatusAlergia.REJEITADA, a.getStatusAlergia());
        assertEquals(Long.valueOf(99L), a.getIdAprovador());
        assertEquals("Duplicada no sistema", a.getObservacaoEnfermeiro());
        assertNotNull(a.getDataUltimaAlteracao());
    }

    @Test(expected = IllegalStateException.class)
    public void aprovar_deveLancarExcecaoSeNaoEstiverInformada() {
        Alergia a = alergiaInformada();
        a.aprovar(99L, "ok");
        a.aprovar(99L, "tentativa dupla");
    }

    @Test(expected = IllegalStateException.class)
    public void rejeitar_deveLancarExcecaoSeNaoEstiverInformada() {
        Alergia a = alergiaInformada();
        a.rejeitar(99L, "ok");
        a.rejeitar(99L, "tentativa dupla");
    }

    @Test(expected = IllegalStateException.class)
    public void aprovar_deveLancarExcecaoSeJaRejeitada() {
        Alergia a = alergiaInformada();
        a.rejeitar(99L, "rejeitada");
        a.aprovar(99L, "tentativa de aprovar após rejeição");
    }

    @Test
    public void validarExclusao_devePermitirExclusaoDeInformada() {
        Alergia a = alergiaInformada();
        a.validarExclusao();
    }

    @Test
    public void validarExclusao_devePermitirExclusaoDeRejeitada() {
        Alergia a = alergiaInformada();
        a.rejeitar(99L, "rejeitada");
        a.validarExclusao();
    }

    @Test(expected = IllegalStateException.class)
    public void validarExclusao_deveLancarExcecaoSeAprovada() {
        Alergia a = alergiaInformada();
        a.aprovar(99L, "aprovada");
        a.validarExclusao();
    }
}
