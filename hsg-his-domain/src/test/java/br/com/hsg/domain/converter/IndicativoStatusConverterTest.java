package br.com.hsg.domain.converter;

import br.com.hsg.domain.enums.IndicativoStatus;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class IndicativoStatusConverterTest {

    private IndicativoStatusConverter converter;

    @Before
    public void setUp() {
        converter = new IndicativoStatusConverter();
    }

    @Test
    public void convertToDatabaseColumn_deveRetornarValor() {
        assertEquals("A", converter.convertToDatabaseColumn(IndicativoStatus.A));
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    public void convertToEntityAttribute_deveRetornarEnum() {
        assertEquals(IndicativoStatus.I, converter.convertToEntityAttribute("I"));
        assertNull(converter.convertToEntityAttribute(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertToEntityAttribute_deveLancarExcecaoSeValorInvalido() {
        converter.convertToEntityAttribute("X");
    }
}
