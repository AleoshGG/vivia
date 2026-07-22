package aleosh.online.vivia.core.security.encryption;

public interface EncryptionService {
    /**
     * Cifra un valor usando AES-256-GCM.
     *
     * @param plaintext Valor a cifrar
     * @return Ciphertext codificado en Base64 (formato: Base64(IV || ciphertext || tag))
     * @throws IllegalArgumentException si plaintext es null
     */
    String encrypt(String plaintext);

    /**
     * Descifra un valor previamente cifrado.
     *
     * @param ciphertext Ciphertext codificado en Base64
     * @return Valor original en texto plano
     * @throws IllegalArgumentException si ciphertext es inválido o no puede descifrarse
     */
    String decrypt(String ciphertext);
}
