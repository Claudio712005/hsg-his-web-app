package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.IndicativoStatus;
import br.com.hsg.domain.vo.Email;
import br.com.hsg.domain.vo.NomeCompleto;
import org.junit.Test;

import static org.junit.Assert.*;

public class AdminTest {

    private ContaUsuario contaUsuario() {
        return new ContaUsuario(null, "ad-uuid-001", "admin.hsg");
    }

    @Test
    public void criar_deveDefinirStatusAtivoEDataCadastro() {
        NomeCompleto nome = new NomeCompleto("Admin", "HSG");
        Email email       = new Email("admin@hsg.com.br");
        Admin admin       = Admin.criar(nome, email, contaUsuario());

        assertEquals("Admin HSG", admin.getNomeCompleto());
        assertEquals("admin@hsg.com.br", admin.getEmail());
        assertEquals(IndicativoStatus.A, admin.getStatus());
        assertNotNull(admin.getDataCadastro());
        assertNotNull(admin.getContaUsuario());
    }

    @Test
    public void podeLogar_deveRetornarTrueQuandoAtivo() {
        Admin admin = Admin.criar(null, null, contaUsuario());
        assertTrue(admin.podeLogar());
    }

    @Test(expected = NullPointerException.class)
    public void criar_deveLancarExcecaoQuandoContaUsuarioNula() {
        Admin.criar(null, null, null);
    }
}
