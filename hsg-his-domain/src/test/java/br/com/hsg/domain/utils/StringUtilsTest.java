package br.com.hsg.domain.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringUtilsTest {

    @Test
    public void isNullOrEmpty_deveRetornarTrueParaNuloOuVazio() {
        assertTrue(StringUtils.isNullOrEmpty(null));
        assertTrue(StringUtils.isNullOrEmpty(""));
    }

    @Test
    public void isNullOrEmpty_deveRetornarFalseParaTextoPreenchido() {
        assertFalse(StringUtils.isNullOrEmpty("abc"));
    }
}
