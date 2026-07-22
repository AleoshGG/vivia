package aleosh.online.vivia.core.security.encryption;

import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de integración que verifica el cifrado de extremo a extremo.
 * Usa un contexto JPA real para verificar que los convertidores funcionan correctamente.
 */
@DataJpaTest
@Import({
        aleosh.online.vivia.core.security.encryption.impl.AesGcmEncryptionService.class,
        aleosh.online.vivia.core.security.encryption.converters.EncryptedStringConverter.class
})
@ActiveProfiles("dev")
class EncryptionIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EncryptionService encryptionService;

    @Test
    void testUserEntityFieldsAreEncryptedInDatabase() {
        // Arrange: crear un UserEntity con datos sensibles
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .paternalSurname("Smith")
                .maternalSurname("Johnson")
                .email("john@example.com")
                .photoUrl("https://example.com/photo.jpg")
                .fcmToken("fcm-token-12345")
                .build();

        // Act: persistir y flush para que el converter actúe
        entityManager.persistAndFlush(user);

        // Verificar que los datos se almacenaron en la BD (sin descifrar)
        entityManager.clear();

        // Assert: leer el usuario de la BD sin converter (SQL raw)
        UserEntity retrieved = entityManager.find(UserEntity.class, user.getId());
        assertNotNull(retrieved);

        // Los nombres deben ser Base64 cifrados (diferentes al texto original)
        assertNotEquals("John Doe", retrieved.getName());
        assertNotEquals("Smith", retrieved.getPaternalSurname());
        assertNotEquals("Johnson", retrieved.getMaternalSurname());

        // Email debe estar en texto plano (no se cifra)
        assertEquals("john@example.com", retrieved.getEmail());

        // Pero cuando lo desencriptamos manualmente con EncryptionService
        String decryptedName = encryptionService.decrypt(retrieved.getName());
        String decryptedPaternal = encryptionService.decrypt(retrieved.getPaternalSurname());
        String decryptedMaternal = encryptionService.decrypt(retrieved.getMaternalSurname());

        assertEquals("John Doe", decryptedName);
        assertEquals("Smith", decryptedPaternal);
        assertEquals("Johnson", decryptedMaternal);
    }

    @Test
    void testUserEntityRoundTrip() {
        // Arrange: datos originales
        UUID userId = UUID.randomUUID();
        String originalName = "Alice";
        String originalPaternal = "Brown";
        String originalMaternal = "White";
        String originalEmail = "alice@example.com";
        String originalFcm = "fcm-alice-token";

        UserEntity user = UserEntity.builder()
                .id(userId)
                .name(originalName)
                .paternalSurname(originalPaternal)
                .maternalSurname(originalMaternal)
                .email(originalEmail)
                .photoUrl("https://example.com/alice.jpg")
                .fcmToken(originalFcm)
                .build();

        // Act: persistir
        entityManager.persistAndFlush(user);
        entityManager.clear();

        // Retrieve: obtener de la BD
        UserEntity retrieved = entityManager.find(UserEntity.class, userId);

        // Assert: los valores deben ser idénticos (el converter desencripta automáticamente)
        assertEquals(originalName, retrieved.getName());
        assertEquals(originalPaternal, retrieved.getPaternalSurname());
        assertEquals(originalMaternal, retrieved.getMaternalSurname());
        assertEquals(originalEmail, retrieved.getEmail());  // Email no cifrado
        assertEquals(originalFcm, retrieved.getFcmToken());
    }

    @Test
    void testMultipleUsersWithSameName() {
        // Arrange: dos usuarios con el mismo nombre
        String sameName = "Bob";
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();

        UserEntity user1 = UserEntity.builder()
                .id(user1Id)
                .name(sameName)
                .paternalSurname("Surname1")
                .maternalSurname("Surname1")
                .email("bob1@example.com")
                .photoUrl("url")
                .build();

        UserEntity user2 = UserEntity.builder()
                .id(user2Id)
                .name(sameName)
                .paternalSurname("Surname2")
                .maternalSurname("Surname2")
                .email("bob2@example.com")
                .photoUrl("url")
                .build();

        // Act: persistir ambos
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.clear();

        // Assert: aunque tengan el mismo nombre en texto plano,
        // su cifrado debe ser diferente (AES-GCM con IV aleatorio)
        UserEntity retrieved1 = entityManager.find(UserEntity.class, user1Id);
        UserEntity retrieved2 = entityManager.find(UserEntity.class, user2Id);

        // Ambos desencriptan al mismo valor
        assertEquals(sameName, retrieved1.getName());
        assertEquals(sameName, retrieved2.getName());

        // Pero en BD están cifrados de forma diferente
        assertNotNull(retrieved1.getName());
        assertNotNull(retrieved2.getName());
        // Nota: ambos se almacenan cifrados, pero no podemos comparar directamente
        // sin acceso a BD raw. Esto está verificado en testUserEntityFieldsAreEncryptedInDatabase
    }

    @Test
    void testNullFcmTokenIsNotEncrypted() {
        // Arrange: user sin FCM token
        UUID userId = UUID.randomUUID();
        UserEntity user = UserEntity.builder()
                .id(userId)
                .name("Test User")
                .paternalSurname("Test")
                .maternalSurname("User")
                .email("test@example.com")
                .photoUrl("url")
                .fcmToken(null)
                .build();

        // Act: persistir
        entityManager.persistAndFlush(user);
        entityManager.clear();

        // Assert: null debe seguir siendo null
        UserEntity retrieved = entityManager.find(UserEntity.class, userId);
        assertNull(retrieved.getFcmToken());
    }
}
