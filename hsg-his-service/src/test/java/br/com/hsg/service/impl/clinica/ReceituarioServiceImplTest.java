package br.com.hsg.service.impl.clinica;

import br.com.hsg.dao.ConsultaDAO;
import br.com.hsg.dao.ReceitaDAO;
import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.domain.entity.Receita;
import br.com.hsg.domain.enums.StatusConsulta;
import br.com.hsg.domain.enums.TipoResponsavel;
import br.com.hsg.service.facade.clinica.ReceituarioServiceFacade.ItemDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ReceituarioServiceImplTest {

    @Mock private ReceitaDAO  receitaDAO;
    @Mock private ConsultaDAO consultaDAO;

    @InjectMocks private ReceituarioServiceImpl service;

    private Consulta consulta;
    private Medico medico;
    private Paciente paciente;

    @Before
    public void setUp() {
        medico = mock(Medico.class);
        when(medico.getId()).thenReturn(7L);
        paciente = mock(Paciente.class);
        when(paciente.getId()).thenReturn(100L);
        consulta = mock(Consulta.class);
        when(consulta.getMedico()).thenReturn(medico);
        when(consulta.getPaciente()).thenReturn(paciente);
        when(consulta.getStatus()).thenReturn(StatusConsulta.REALIZADA);
        when(consultaDAO.buscarPorIdComMedico(1L)).thenReturn(consulta);
        when(receitaDAO.salvar(any(Receita.class))).thenAnswer(i -> i.getArgument(0));
    }

    private List<ItemDTO> umItem() {
        return Collections.singletonList(new ItemDTO("Dipirona 500mg", "1 cp 6/6h", null, "R51"));
    }

    @Test
    public void emitir_medicoResponsavel_consultaRealizada_deveSalvar() {
        Receita r = service.emitir(1L, 7L, umItem());
        assertNotNull(r);
        verify(receitaDAO).salvar(any(Receita.class));
    }

    @Test
    public void emitir_consultaConfirmada_deveSalvar() {
        when(consulta.getStatus()).thenReturn(StatusConsulta.CONFIRMADA);
        service.emitir(1L, 7L, umItem());
        verify(receitaDAO).salvar(any(Receita.class));
    }

    @Test(expected = IllegalStateException.class)
    public void emitir_consultaCancelada_deveRecusar() {
        when(consulta.getStatus()).thenReturn(StatusConsulta.CANCELADA);
        service.emitir(1L, 7L, umItem());
    }

    @Test(expected = IllegalStateException.class)
    public void emitir_consultaFalta_deveRecusar() {
        when(consulta.getStatus()).thenReturn(StatusConsulta.FALTOU);
        service.emitir(1L, 7L, umItem());
    }

    @Test(expected = IllegalStateException.class)
    public void emitir_medicoNaoResponsavel_deveRecusar() {
        service.emitir(1L, 999L, umItem());
    }

    @Test(expected = IllegalArgumentException.class)
    public void emitir_idMedicoNull_deveLancar() {
        service.emitir(1L, null, umItem());
    }

    @Test(expected = IllegalArgumentException.class)
    public void emitir_listaVazia_deveLancar() {
        service.emitir(1L, 7L, Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void emitir_consultaNaoEncontrada_deveLancar() {
        when(consultaDAO.buscarPorIdComMedico(99L)).thenReturn(null);
        service.emitir(99L, 7L, umItem());
    }

    @Test
    public void emitir_jaTinhaReceita_deveChamarInativarBulk() {
        when(receitaDAO.inativarAtivasPorConsulta(1L)).thenReturn(1);
        service.emitir(1L, 7L, umItem());
        verify(receitaDAO).inativarAtivasPorConsulta(1L);
        verify(receitaDAO).salvar(any(Receita.class));
    }

    @Test
    public void emitir_semReceitaAnterior_naoInativaNada() {
        when(receitaDAO.inativarAtivasPorConsulta(1L)).thenReturn(0);
        service.emitir(1L, 7L, umItem());
        verify(receitaDAO).inativarAtivasPorConsulta(1L);
        verify(receitaDAO).salvar(any(Receita.class));
    }

    @Test
    public void buscarPorConsulta_delegaAoDao() {
        Receita r = mock(Receita.class);
        when(receitaDAO.buscarAtivaPorConsulta(1L)).thenReturn(r);
        org.junit.Assert.assertSame(r, service.buscarPorConsulta(1L));
    }

    @Test
    public void buscarParaPdf_enfermeiro_deveRetornar() {
        Receita r = mock(Receita.class);
        when(r.getConsulta()).thenReturn(consulta);
        when(receitaDAO.buscarAtivaPorConsulta(1L)).thenReturn(r);
        org.junit.Assert.assertSame(r, service.buscarParaPdf(1L, 33L, TipoResponsavel.ENFERMEIRO));
    }

    @Test
    public void buscarParaPdf_admin_deveRetornar() {
        Receita r = mock(Receita.class);
        when(receitaDAO.buscarAtivaPorConsulta(1L)).thenReturn(r);
        org.junit.Assert.assertSame(r, service.buscarParaPdf(1L, 1L, TipoResponsavel.ADMIN));
    }

    @Test
    public void buscarParaPdf_medicoResponsavel_deveRetornar() {
        Receita r = mock(Receita.class);
        when(r.getConsulta()).thenReturn(consulta);
        when(receitaDAO.buscarAtivaPorConsulta(1L)).thenReturn(r);
        org.junit.Assert.assertSame(r, service.buscarParaPdf(1L, 7L, TipoResponsavel.MEDICO));
    }

    @Test(expected = IllegalStateException.class)
    public void buscarParaPdf_medicoDeOutro_deveRecusar() {
        Receita r = mock(Receita.class);
        when(r.getConsulta()).thenReturn(consulta);
        when(receitaDAO.buscarAtivaPorConsulta(1L)).thenReturn(r);
        service.buscarParaPdf(1L, 999L, TipoResponsavel.MEDICO);
    }

    @Test
    public void buscarParaPdf_pacienteProprio_deveRetornar() {
        Receita r = mock(Receita.class);
        when(r.getConsulta()).thenReturn(consulta);
        when(receitaDAO.buscarAtivaPorConsulta(1L)).thenReturn(r);
        org.junit.Assert.assertSame(r, service.buscarParaPdf(1L, 100L, TipoResponsavel.PACIENTE));
    }

    @Test(expected = IllegalStateException.class)
    public void buscarParaPdf_pacienteOutro_deveRecusar() {
        Receita r = mock(Receita.class);
        when(r.getConsulta()).thenReturn(consulta);
        when(receitaDAO.buscarAtivaPorConsulta(1L)).thenReturn(r);
        service.buscarParaPdf(1L, 999L, TipoResponsavel.PACIENTE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void buscarParaPdf_naoExiste_deveLancar() {
        when(receitaDAO.buscarAtivaPorConsulta(1L)).thenReturn(null);
        service.buscarParaPdf(1L, 1L, TipoResponsavel.ADMIN);
    }

    @Test
    public void emitir_multiplosItens_devePreservarOrdem() {
        List<ItemDTO> itens = Arrays.asList(
                new ItemDTO("Dipirona", "1 cp 6/6h", null, null),
                new ItemDTO("SRO", "1 sachê pós evacuação", "Hidratação", null));
        service.emitir(1L, 7L, itens);
        verify(receitaDAO).salvar(any(Receita.class));
    }

    @Test
    public void excluir_medicoResponsavel_deveInativar() {
        Receita ativa = mock(Receita.class);
        when(receitaDAO.buscarAtivaPorConsulta(1L)).thenReturn(ativa);

        service.excluir(1L, 7L);

        verify(ativa).inativar();
        verify(receitaDAO).atualizar(ativa);
    }

    @Test(expected = IllegalStateException.class)
    public void excluir_medicoNaoResponsavel_deveRecusar() {
        service.excluir(1L, 999L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void excluir_semReceitaAtiva_deveLancar() {
        when(receitaDAO.buscarAtivaPorConsulta(1L)).thenReturn(null);
        service.excluir(1L, 7L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void excluir_idMedicoNull_deveLancar() {
        service.excluir(1L, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void excluir_consultaNaoEncontrada_deveLancar() {
        when(consultaDAO.buscarPorIdComMedico(99L)).thenReturn(null);
        service.excluir(99L, 7L);
    }
}
