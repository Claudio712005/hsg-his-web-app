package br.com.hsg.web.bean.session;

import br.com.hsg.web.dto.response.AdminResponseDTO;
import br.com.hsg.web.dto.response.PacienteResponseDTO;
import br.com.hsg.web.dto.response.UsuarioClinicaDTO;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class BeanSessaoTest {

    private BeanSessao bean;

    @Before
    public void setUp() {
        bean = new BeanSessao();
    }

    @Test
    public void isLogado_deveRetornarFalseQuandoSemSessao() {
        assertFalse(bean.isLogado());
    }

    @Test
    public void isLogado_deveRetornarTrueComPaciente() {
        bean.setPaciente(mock(PacienteResponseDTO.class));
        assertTrue(bean.isLogado());
        assertTrue(bean.isLogadoComoPaciente());
        assertFalse(bean.isLogadoComoClinica());
        assertFalse(bean.isLogadoComoAdmin());
    }

    @Test
    public void isLogado_deveRetornarTrueComUsuarioClinica() {
        bean.setUsuarioClinica(mock(UsuarioClinicaDTO.class));
        assertTrue(bean.isLogado());
        assertFalse(bean.isLogadoComoPaciente());
        assertTrue(bean.isLogadoComoClinica());
        assertFalse(bean.isLogadoComoAdmin());
    }

    @Test
    public void isLogado_deveRetornarTrueComAdmin() {
        bean.setAdmin(mock(AdminResponseDTO.class));
        assertTrue(bean.isLogado());
        assertFalse(bean.isLogadoComoPaciente());
        assertFalse(bean.isLogadoComoClinica());
        assertTrue(bean.isLogadoComoAdmin());
    }

    @Test
    public void encerrarSessao_deveLimparTodosOsDados() {
        bean.setPaciente(mock(PacienteResponseDTO.class));
        bean.setUsuarioClinica(mock(UsuarioClinicaDTO.class));
        bean.setAdmin(mock(AdminResponseDTO.class));

        bean.encerrarSessao();

        assertFalse(bean.isLogado());
        assertNull(bean.getPaciente());
        assertNull(bean.getUsuarioClinica());
        assertNull(bean.getAdmin());
    }

    @Test
    public void encerrarSessao_naoDeveLancarExcecaoComSessaoVazia() {
        bean.encerrarSessao();
        assertFalse(bean.isLogado());
    }
}
