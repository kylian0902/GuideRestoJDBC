package ch.hearc.ig.guideresto.persistence.jpa.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TFBooleanConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean value) {
        return Boolean.TRUE.equals(value) ? "T" : "F";
    }

    @Override
    public Boolean convertToEntityAttribute(String db) {
        return "T".equalsIgnoreCase(db);
    }
}
