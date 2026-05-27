package br.com.hsg.domain.entity;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class MedicoEspecialidadeTest {

    private final Medico medico = mock(Medico.class);
    private final Especialidade esp = mock(Especialidade.class);

    @Test
    public void criar_devePersistirPrincipalCorretamente() {
        MedicoEspecialidade pr = MedicoEspecialidade.criar(medico, esp, true);
        MedicoEspecialidade sec = MedicoEspecialidade.criar(medico, esp, false);

        assertTrue(pr.isPrincipal());
        assertFalse(sec.isPrincipal());
        assertNotNull(pr.getDataCadastro());
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarQuandoMedicoNulo() {
        MedicoEspecialidade.criar(null, esp, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void criar_deveLancarQuandoEspecialidadeNula() {
        MedicoEspecialidade.criar(medico, null, true);
    }

    @Test
    public void marcarEDesmarcarPrincipal_devemAlternarFlag() {
        MedicoEspecialidade me = MedicoEspecialidade.criar(medico, esp, false);

        me.marcarComoPrincipal();
        assertTrue(me.isPrincipal());

        me.desmarcarPrincipal();
        assertFalse(me.isPrincipal());
    }
}
