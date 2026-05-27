package br.com.hsg.domain.converter;

import br.com.hsg.domain.enums.DiaSemana;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DiaSemanaConverterTest {

    private final DiaSemanaConverter conv = new DiaSemanaConverter();

    @Test
    public void convertToDatabaseColumn_deveRetornarValorOuNull() {
        assertEquals(Integer.valueOf(1), conv.convertToDatabaseColumn(DiaSemana.SEGUNDA));
        assertEquals(Integer.valueOf(7), conv.convertToDatabaseColumn(DiaSemana.DOMINGO));
        assertNull(conv.convertToDatabaseColumn(null));
    }

    @Test
    public void convertToEntityAttribute_deveRetornarEnumOuNull() {
        assertEquals(DiaSemana.SEGUNDA, conv.convertToEntityAttribute(1));
        assertEquals(DiaSemana.DOMINGO, conv.convertToEntityAttribute(7));
        assertNull(conv.convertToEntityAttribute(null));
    }
}
