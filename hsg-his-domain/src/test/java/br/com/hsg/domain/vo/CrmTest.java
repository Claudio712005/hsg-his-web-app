package br.com.hsg.domain.vo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CrmTest {

    @Test
    public void deveCriarCrmEFormatar() {
        Crm crm = new Crm("12345", "sp");
        assertEquals("12345", crm.getNumero());
        assertEquals("SP", crm.getUf());
        assertEquals("CRM-SP 12345", crm.getFormatado());
    }

    @Test(expected = IllegalArgumentException.class)
    public void deveLancarExcecaoQuandoNumeroVazio() {
        new Crm("  ", "SP");
    }

    @Test(expected = IllegalArgumentException.class)
    public void deveLancarExcecaoQuandoUfNula() {
        new Crm("12345", null);
    }
}
