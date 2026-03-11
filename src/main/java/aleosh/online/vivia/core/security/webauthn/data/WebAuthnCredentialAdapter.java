package aleosh.online.vivia.core.security.webauthn.data;

import aleosh.online.vivia.features.users.lessee.data.entities.LesseeEntity;
import aleosh.online.vivia.features.users.lessee.data.repositories.LesseeRepository;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.lessor.data.repositories.LessorRepository;
import aleosh.online.vivia.features.users.lessor.data.repositories.PasskeyCredentialRepository;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Component
public class WebAuthnCredentialAdapter implements CredentialRepository {

    private final LessorRepository lessorRepository;
    private final LesseeRepository lesseeRepository;
    private final PasskeyCredentialRepository passkeyRepository;

    public WebAuthnCredentialAdapter(
            LessorRepository lessorRepository,
            LesseeRepository lesseeRepository,
            PasskeyCredentialRepository passkeyRepository) {
        this.lessorRepository = lessorRepository;
        this.lesseeRepository = lesseeRepository;
        this.passkeyRepository = passkeyRepository;
    }

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        // En un flujo sin teclear usuario (Discoverable), esto casi no se usa, pero lo implementamos por estándar.
        if (username.contains("@")) {
            return lesseeRepository.findByEmail(username)
                    .map(l -> l.getCredentials().stream()
                            .map(c -> PublicKeyCredentialDescriptor.builder().id(new ByteArray(c.getCredentialId())).build())
                            .collect(Collectors.toSet()))
                    .orElse(Set.of());
        }
        return lessorRepository.findByCompanyName(username)
                .map(l -> l.getCredentials().stream()
                        .map(c -> PublicKeyCredentialDescriptor.builder().id(new ByteArray(c.getCredentialId())).build())
                        .collect(Collectors.toSet()))
                .orElse(Set.of());
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        if (username.contains("@")) {
            return lesseeRepository.findByEmail(username).map(l -> new ByteArray(l.getUserHandle()));
        }
        return lessorRepository.findByCompanyName(username).map(l -> new ByteArray(l.getUserHandle()));
    }

    // Este es el método MÁS IMPORTANTE. A partir de una llave, Yubico sabrá quién es el usuario.
    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        Optional<LessorEntity> lessor = lessorRepository.findByUserHandle(userHandle.getBytes());
        if (lessor.isPresent()) return Optional.of(lessor.get().getCompanyName());

        Optional<LesseeEntity> lessee = lesseeRepository.findByUserHandle(userHandle.getBytes());
        if (lessee.isPresent()) return Optional.of(lessee.get().getEmail());

        return Optional.empty();
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        return passkeyRepository.findById(credentialId.getBytes())
                .map(cred -> RegisteredCredential.builder()
                        .credentialId(credentialId)
                        .userHandle(userHandle)
                        .publicKeyCose(new ByteArray(cred.getPublicKey()))
                        .signatureCount(cred.getSignCount())
                        .build());
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        return passkeyRepository.findById(credentialId.getBytes())
                .map(cred -> {
                    byte[] handle = cred.getLessor() != null ? cred.getLessor().getUserHandle() : cred.getLessee().getUserHandle();
                    return Set.of(RegisteredCredential.builder()
                            .credentialId(credentialId)
                            .userHandle(new ByteArray(handle))
                            .publicKeyCose(new ByteArray(cred.getPublicKey()))
                            .signatureCount(cred.getSignCount())
                            .build());
                })
                .orElse(Set.of());
    }
}