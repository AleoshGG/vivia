package aleosh.online.vivia.core.security.encryption.converters;

import aleosh.online.vivia.core.security.encryption.EncryptionService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;

@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {
    private static EncryptionService encryptionService;

    @Autowired
    public void setEncryptionService(EncryptionService service) {
        encryptionService = service;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (encryptionService == null) {
            throw new RuntimeException("EncryptionService no está inyectado en EncryptedStringConverter");
        }
        if (attribute == null || attribute.isBlank()) {
            return attribute;  // null/blank se almacenan como-están
        }
        return encryptionService.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (encryptionService == null) {
            throw new RuntimeException("EncryptionService no está inyectado en EncryptedStringConverter");
        }
        if (dbData == null || dbData.isBlank()) {
            return dbData;  // null/blank se retornan como-están
        }
        return encryptionService.decrypt(dbData);
    }
}
