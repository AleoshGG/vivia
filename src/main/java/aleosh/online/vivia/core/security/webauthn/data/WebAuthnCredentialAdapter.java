package aleosh.online.vivia.core.security.webauthn.data;

import aleosh.online.vivia.features.auth.data.entities.WebAuthnCredentialEntity;
import aleosh.online.vivia.features.auth.data.repositories.WebAuthnCredentialRepository;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Transactional(readOnly = true)
@Component
public class WebAuthnCredentialAdapter implements CredentialRepository {

    private final WebAuthnCredentialRepository webAuthnCredentialRepository;

    public WebAuthnCredentialAdapter(WebAuthnCredentialRepository webAuthnCredentialRepository) {
        this.webAuthnCredentialRepository = webAuthnCredentialRepository;
    }

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        // No usado en modo "discoverable credentials" (solo huella sin username)
        return Set.of();
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        // No usado en discoverable credentials
        return Optional.empty();
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        // Convertir userHandle (UUID en bytes) a String
        String userIdStr = new String(userHandle.getBytes(), StandardCharsets.UTF_8);

        try {
            UUID userId = UUID.fromString(userIdStr);

            // Buscar credencial por user_id
            return webAuthnCredentialRepository.findByUser_Id(userId)
                    .map(credential -> credential.getUser().getEmail());
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        String credIdBase64 = credentialId.getBase64Url();

        return webAuthnCredentialRepository
                .findByCredentialId(credIdBase64)
                .map(entity -> RegisteredCredential.builder()
                        .credentialId(credentialId)
                        .userHandle(userHandle)
                        .publicKeyCose(ByteArray.fromBase64(entity.getPublicKey()))
                        .signatureCount(entity.getSignCount())
                        .build()
                );
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        String credIdBase64 = credentialId.getBase64Url();

        return webAuthnCredentialRepository
                .findByCredentialId(credIdBase64)
                .map(entity -> RegisteredCredential.builder()
                        .credentialId(credentialId)
                        .userHandle(new ByteArray(entity.getUser().getId().toString().getBytes(StandardCharsets.UTF_8)))
                        .publicKeyCose(ByteArray.fromBase64(entity.getPublicKey()))
                        .signatureCount(entity.getSignCount())
                        .build()
                )
                .map(Set::of)
                .orElse(Set.of());
    }
}