package br.com.hsg.domain.converter;

import br.com.hsg.domain.enums.DiaSemana;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = false)
public class DiaSemanaConverter implements AttributeConverter<DiaSemana, Integer> {

    @Override
    public Integer convertToDatabaseColumn(DiaSemana atributo) {
        return atributo != null ? atributo.getValor() : null;
    }

    @Override
    public DiaSemana convertToEntityAttribute(Integer coluna) {
        return coluna != null ? DiaSemana.fromValor(coluna) : null;
    }
}
