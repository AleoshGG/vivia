package db.migration;

import aleosh.online.vivia.core.security.encryption.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Helper que proporciona acceso estático a EncryptionService para migraciones Java de Flyway.
 *
 * Flyway instancia migraciones sin parámetros, así que no puede inyectar dependencias directamente.
 * Este singleton almacena la referencia a EncryptionService en un campo estático, permitiendo
 * que V30__encrypt_existing_pii lo acceda via PiiEncryptionHelper.encrypt().
 */
@Component
public class PiiEncryptionHelper {
    private static EncryptionService encryptionService;

    @Autowired
    public PiiEncryptionHelper(EncryptionService service) {
        PiiEncryptionHelper.encryptionService = service;
    }

    /**
     * Cifra un valor usando el EncryptionService inyectado en Spring.
     */
    public static String encrypt(String plaintext) {
        if (encryptionService == null) {
            throw new RuntimeException(
                    "EncryptionService no está disponible. PiiEncryptionHelper no fue inicializado por Spring."
            );
        }
        return encryptionService.encrypt(plaintext);
    }
}
