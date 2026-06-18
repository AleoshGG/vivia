package aleosh.online.vivia.core.security.webauthn.data;

import aleosh.online.vivia.features.auth.data.entities.WebAuthnCredentialEntity;
import aleosh.online.vivia.features.auth.data.repositories.WebAuthnCredentialRepository;
import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import aleosh.online.vivia.features.users.users.data.repositories.UserRepository;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Component
public class WebAuthnCredentialAdapter implements CredentialRepository {

    private final WebAuthnCredentialRepository webAuthnCredentialRepository;
    private final UserRepository userRepository;

    public WebAuthnCredentialAdapter(
            WebAuthnCredentialRepository webAuthnCredentialRepository,
            UserRepository userRepository
    ) {
        this.webAuthnCredentialRepository = webAuthnCredentialRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        // Buscar usuario por email (username)
        Optional<UserEntity> userOpt = userRepository.findByEmail(username);

        if (userOpt.isEmpty()) {
            return Set.of();
        }

        // Buscar todas las credenciales WebAuthn del usuario
        List<WebAuthnCredentialEntity> credentials = webAuthnCredentialRepository.findByUser(userOpt.get());

        // Convertir a PublicKeyCredentialDescriptor
        return credentials.stream()
            .map(cred -> {
                try {
                    return PublicKeyCredentialDescriptor.builder()
                        .id(ByteArray.fromBase64Url(cred.getCredentialId()))
                        .build();
                } catch (Exception e) {
                    // Si hay error al decodificar, omitir esta credencial
                    return null;
                }
            })
            .filter(desc -> desc != null)
            .collect(Collectors.toSet());
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        // Buscar usuario por email y devolver su ID como userHandle
        return userRepository.findByEmail(username)
            .map(user -> new ByteArray(user.getId().toString().getBytes(StandardCharsets.UTF_8)));
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