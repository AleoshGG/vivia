package aleosh.online.vivia.core.security.webauthn.data;

import aleosh.online.vivia.features.users.lessee.data.entities.LesseeEntity;
import aleosh.online.vivia.features.users.lessee.data.repositories.LesseeRepository;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.lessor.data.repositories.LessorRepository;
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
    //private final PasskeyCredentialRepository passkeyRepository;

    public WebAuthnCredentialAdapter(
            LessorRepository lessorRepository,
            LesseeRepository lesseeRepository) {
        this.lessorRepository = lessorRepository;
        this.lesseeRepository = lesseeRepository;
        //this.passkeyRepository = passkeyRepository;
    }

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        return Set.of();
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        return Optional.empty();
    }

    // Este es el método MÁS IMPORTANTE. A partir de una llave, Yubico sabrá quién es el usuario.
    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        return Optional.empty();
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        return Optional.empty(); 
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        return Set.of();
    }
}