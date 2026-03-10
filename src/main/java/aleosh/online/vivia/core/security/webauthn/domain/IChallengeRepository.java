package aleosh.online.vivia.core.security.webauthn.domain;

import com.yubico.webauthn.data.ByteArray;
import java.util.Optional;

public interface IChallengeRepository {
    void saveChallenge(String identifier, ByteArray challenge);
    Optional<ByteArray> getChallenge(String identifier);
    void removeChallenge(String identifier);
}