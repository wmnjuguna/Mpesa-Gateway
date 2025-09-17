package io.github.wmjuguna.daraja.utils.converters;

import io.github.wmjuguna.daraja.utils.Enums.MpesaResponseType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MpesaResponseTypeConverter implements AttributeConverter<MpesaResponseType, String> {

    @Override
    public String convertToDatabaseColumn(MpesaResponseType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getResponse();
    }

    @Override
    public MpesaResponseType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        for (MpesaResponseType type : MpesaResponseType.values()) {
            if (type.getResponse().equals(dbData)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unknown MpesaResponseType value: " + dbData);
    }
}