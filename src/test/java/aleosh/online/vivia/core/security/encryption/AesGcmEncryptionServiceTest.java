package aleosh.online.vivia.core.security.encryption;

import aleosh.online.vivia.core.security.encryption.impl.AesGcmEncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AesGcmEncryptionServiceTest {
    private static final String TEST_ENCRYPTION_KEY = "sNsHYaxRC2x4ZVAOtBiSupo6m/sl7FrLnBDn49al0vQ=";
    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new AesGcmEncryptionService(TEST_ENCRYPTION_KEY);
    }

    @Test
    void testEncryptDecryptSimpleString() {
        String plaintext = "Hello, World!";

        String encrypted = encryptionService.encrypt(plaintext);
        String decrypted = encryptionService.decrypt(encrypted);

        assertEquals(plaintext, decrypted);
        assertNotEquals(plaintext, encrypted);  // Cifrado debe ser diferente
    }

    @Test
    void testEncryptNull() {
        String result = encryptionService.encrypt(null);
        assertNull(result);
    }

    @Test
    void testEncryptBlank() {
        String result = encryptionService.encrypt("   ");
        assertEquals("   ", result);  // blank se retorna sin cifrar
    }

    @Test
    void testDecryptNull() {
        String result = encryptionService.decrypt(null);
        assertNull(result);
    }

    @Test
    void testDecryptBlank() {
        String result = encryptionService.decrypt("");
        assertEquals("", result);
    }

    @Test
    void testEncryptSameValueMultipleTimes_DifferentResults() {
        String plaintext = "Test user email";

        String encrypted1 = encryptionService.encrypt(plaintext);
        String encrypted2 = encryptionService.encrypt(plaintext);

        // AES-GCM usa IV aleatorio, así que dos cifrados del mismo valor deben ser diferentes
        assertNotEquals(encrypted1, encrypted2);

        // Pero ambos deben descifrar al mismo valor
        assertEquals(plaintext, encryptionService.decrypt(encrypted1));
        assertEquals(plaintext, encryptionService.decrypt(encrypted2));
    }

    @Test
    void testEncryptLongString() {
        String longString = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
                + "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";

        String encrypted = encryptionService.encrypt(longString);
        String decrypted = encryptionService.decrypt(encrypted);

        assertEquals(longString, decrypted);
    }

    @Test
    void testEncryptSpecialCharacters() {
        String specialChars = "user@example.com | +1-234-567-8900 | 中文 | émoji 🔐";

        String encrypted = encryptionService.encrypt(specialChars);
        String decrypted = encryptionService.decrypt(encrypted);

        assertEquals(specialChars, decrypted);
    }

    @Test
    void testDecryptInvalidBase64() {
        String invalidCiphertext = "not-valid-base64!!!";

        assertThrows(RuntimeException.class, () -> encryptionService.decrypt(invalidCiphertext));
    }

    @Test
    void testDecryptTamperedData() {
        String plaintext = "Sensitive data";
        String encrypted = encryptionService.encrypt(plaintext);

        // Tamper con el ciphertext: cambiar un carácter
        String tamperedCiphertext = encrypted.substring(0, encrypted.length() - 2)
                + (encrypted.charAt(encrypted.length() - 2) == 'a' ? 'b' : 'a')
                + encrypted.charAt(encrypted.length() - 1);

        assertThrows(RuntimeException.class, () -> encryptionService.decrypt(tamperedCiphertext));
    }

    @Test
    void testInvalidEncryptionKey_TooShort() {
        String invalidKey = "c2hvcnQ=";  // Base64 de "short" - solo 5 bytes
        assertThrows(IllegalArgumentException.class, () -> new AesGcmEncryptionService(invalidKey));
    }

    @Test
    void testInvalidEncryptionKey_InvalidBase64() {
        String invalidBase64 = "not!!!valid!!!base64";
        assertThrows(IllegalArgumentException.class, () -> new AesGcmEncryptionService(invalidBase64));
    }

    @Test
    void testMissingEncryptionKey() {
        assertThrows(IllegalArgumentException.class, () -> new AesGcmEncryptionService(""));
        assertThrows(IllegalArgumentException.class, () -> new AesGcmEncryptionService(null));
    }

    @Test
    void testEncryptedDataIsBase64Encoded() {
        String plaintext = "Test";
        String encrypted = encryptionService.encrypt(plaintext);

        // El ciphertext debe ser Base64 válido
        assertDoesNotThrow(() -> java.util.Base64.getDecoder().decode(encrypted));
    }

    @Test
    void testEncryptEmptyString() {
        String result = encryptionService.encrypt("");
        assertEquals("", result);
    }
}
