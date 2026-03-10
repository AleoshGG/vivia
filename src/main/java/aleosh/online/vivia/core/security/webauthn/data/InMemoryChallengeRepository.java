package aleosh.online.vivia.core.security.webauthn.data;

import aleosh.online.vivia.core.security.webauthn.domain.IChallengeRepository;
import com.yubico.webauthn.data.ByteArray;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryChallengeRepository implements IChallengeRepository {

    // Usamos ConcurrentHashMap para manejar peticiones simultáneas de forma segura
    private final Map<String, ByteArray> challenges = new ConcurrentHashMap<>();

    @Override
    public void saveChallenge(String identifier, ByteArray challenge) {
        challenges.put(identifier, challenge);
    }

    @Override
    public Optional<ByteArray> getChallenge(String identifier) {
        return Optional.ofNullable(challenges.get(identifier));
    }

    @Override
    public void removeChallenge(String identifier) {
        challenges.remove(identifier);
    }
}