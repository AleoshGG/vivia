package aleosh.online.vivia.core.security.encryption;

import aleosh.online.vivia.core.security.encryption.converters.EncryptedStringConverter;
import aleosh.online.vivia.core.security.encryption.impl.AesGcmEncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptedStringConverterTest {
    private static final String TEST_ENCRYPTION_KEY = "sNsHYaxRC2x4ZVAOtBiSupo6m/sl7FrLnBDn49al0vQ=";
    private EncryptedStringConverter converter;
    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new AesGcmEncryptionService(TEST_ENCRYPTION_KEY);
        converter = new EncryptedStringConverter();
        // Simular inyección de dependencia manualmente
        converter.setEncryptionService(encryptionService);
    }

    @Test
    void testConvertToDatabaseColumn_PlainToEncrypted() {
        String plaintext = "John Doe";

        String encrypted = converter.convertToDatabaseColumn(plaintext);

        assertNotNull(encrypted);
        assertNotEquals(plaintext, encrypted);
        // El resultado debe ser Base64 (contiene solo caracteres válidos)
        assertDoesNotThrow(() -> java.util.Base64.getDecoder().decode(encrypted));
    }

    @Test
    void testConvertToEntityAttribute_EncryptedToPlain() {
        String plaintext = "John Doe";
        String encrypted = encryptionService.encrypt(plaintext);

        String decrypted = converter.convertToEntityAttribute(encrypted);

        assertEquals(plaintext, decrypted);
    }

    @Test
    void testRoundTrip() {
        String original = "Jane Smith";

        String encrypted = converter.convertToDatabaseColumn(original);
        String decrypted = converter.convertToEntityAttribute(encrypted);

        assertEquals(original, decrypted);
    }

    @Test
    void testConvertToDatabaseColumn_Null() {
        String result = converter.convertToDatabaseColumn(null);
        assertNull(result);
    }

    @Test
    void testConvertToDatabaseColumn_Blank() {
        String result = converter.convertToDatabaseColumn("   ");
        assertEquals("   ", result);
    }

    @Test
    void testConvertToEntityAttribute_Null() {
        String result = converter.convertToEntityAttribute(null);
        assertNull(result);
    }

    @Test
    void testConvertToEntityAttribute_Blank() {
        String result = converter.convertToEntityAttribute("");
        assertEquals("", result);
    }

    @Test
    void testMultipleValues_EachEncryptedDifferently() {
        String value1 = "Alice";
        String value2 = "Alice";  // Mismo valor

        String encrypted1 = converter.convertToDatabaseColumn(value1);
        String encrypted2 = converter.convertToDatabaseColumn(value2);

        // Aunque sean iguales, AES-GCM produce diferentes ciphertexts
        assertNotEquals(encrypted1, encrypted2);

        // Pero ambos desencriptan correctamente
        assertEquals(value1, converter.convertToEntityAttribute(encrypted1));
        assertEquals(value2, converter.convertToEntityAttribute(encrypted2));
    }

    @Test
    void testConvertWithSpecialCharacters() {
        String special = "user+test@example.com";

        String encrypted = converter.convertToDatabaseColumn(special);
        String decrypted = converter.convertToEntityAttribute(encrypted);

        assertEquals(special, decrypted);
    }

}
