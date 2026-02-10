package io.github.wmjuguna.daraja.utils.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import io.github.wmjuguna.daraja.utils.PaybillRegistrationStatus;

@Converter(autoApply = true)
public class PaybillRegistrationStatusConverter implements AttributeConverter<PaybillRegistrationStatus, String> {

    @Override
    public String convertToDatabaseColumn(PaybillRegistrationStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public PaybillRegistrationStatus convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        for (PaybillRegistrationStatus status : PaybillRegistrationStatus.values()) {
            if (status.getValue().equalsIgnoreCase(dbData)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown PaybillRegistrationStatus value: " + dbData);
    }
}
