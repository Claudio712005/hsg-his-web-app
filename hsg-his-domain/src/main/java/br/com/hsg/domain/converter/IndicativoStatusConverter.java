package br.com.hsg.domain.converter;

import br.com.hsg.domain.enums.IndicativoStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = false)
public class IndicativoStatusConverter implements AttributeConverter<IndicativoStatus, String> {

    @Override
    public String convertToDatabaseColumn(IndicativoStatus status) {
        if (status == null) return null;
        return status.getValor();
    }

    @Override
    public IndicativoStatus convertToEntityAttribute(String valor) {
        if (valor == null) return null;
        return IndicativoStatus.fromValor(valor);
    }
}