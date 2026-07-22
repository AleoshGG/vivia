package aleosh.online.vivia.core.security.encryption.impl;

import aleosh.online.vivia.core.security.encryption.EncryptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class AesGcmEncryptionService implements EncryptionService {
    private static final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_SIZE_BITS = 256;
    private static final int GCM_NONCE_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final String CHARSET = "UTF-8";

    private final SecretKeySpec secretKey;
    private final SecureRandom secureRandom;

    public AesGcmEncryptionService(@Value("${app.security.encryption-key}") String encryptionKeyBase64) {
        if (encryptionKeyBase64 == null || encryptionKeyBase64.isBlank()) {
            throw new IllegalArgumentException(
                    "ENCRYPTION_KEY debe estar configurada. Usa: export ENCRYPTION_KEY=$(openssl rand -base64 32)"
            );
        }

        byte[] decodedKey;
        try {
            decodedKey = Base64.getDecoder().decode(encryptionKeyBase64);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("ENCRYPTION_KEY no es válido Base64", e);
        }

        if (decodedKey.length != KEY_SIZE_BITS / 8) {
            throw new IllegalArgumentException(
                    "ENCRYPTION_KEY debe ser exactamente 32 bytes (256 bits). Obtenidos: " + decodedKey.length
            );
        }

        this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        this.secureRandom = new SecureRandom();
    }

    @Override
    public String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isBlank()) {
            return plaintext;  // No cifrar null/blank, retornar tal cual
        }

        try {
            byte[] plaintextBytes = plaintext.getBytes(CHARSET);

            // Generar IV aleatorio (12 bytes para GCM)
            byte[] iv = new byte[GCM_NONCE_LENGTH_BYTES];
            secureRandom.nextBytes(iv);

            // Crear cipher y configurar con IV
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

            // Cifrar
            byte[] ciphertext = cipher.doFinal(plaintextBytes);

            // Formato: IV || ciphertext (con tag incluido)
            byte[] result = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(ciphertext, 0, result, iv.length, ciphertext.length);

            // Codificar en Base64 para almacenamiento
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            throw new RuntimeException("Error al cifrar datos", e);
        }
    }

    @Override
    public String decrypt(String ciphertext) {
        if (ciphertext == null || ciphertext.isBlank()) {
            return ciphertext;  // No descifrar null/blank
        }

        try {
            // Decodificar Base64
            byte[] encryptedData = Base64.getDecoder().decode(ciphertext);

            // Extraer IV (primeros 12 bytes)
            byte[] iv = new byte[GCM_NONCE_LENGTH_BYTES];
            System.arraycopy(encryptedData, 0, iv, 0, GCM_NONCE_LENGTH_BYTES);

            // Extraer ciphertext + tag (resto)
            byte[] encryptedBytes = new byte[encryptedData.length - GCM_NONCE_LENGTH_BYTES];
            System.arraycopy(encryptedData, GCM_NONCE_LENGTH_BYTES, encryptedBytes, 0, encryptedBytes.length);

            // Descifrar
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

            byte[] plaintext = cipher.doFinal(encryptedBytes);
            return new String(plaintext, CHARSET);
        } catch (javax.crypto.AEADBadTagException e) {
            throw new RuntimeException("Verificación de integridad fallida: datos cifrados fueron alterados", e);
        } catch (Exception e) {
            throw new RuntimeException("Error al descifrar datos", e);
        }
    }
}
