package aleosh.online.vivia.core.security.webauthn.data;

import aleosh.online.vivia.core.security.webauthn.domain.IChallengeRepository;
import com.yubico.webauthn.data.ByteArray;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Implementación de IChallengeRepository usando Redis
 * para almacenar challenges de WebAuthn con TTL automático
 */
@Repository
@Primary
public class RedisChallengeRepository implements IChallengeRepository {

    private static final String CHALLENGE_PREFIX = "webauthn:challenge:register:";
    private static final long CHALLENGE_TTL_MINUTES = 5;

    private final RedisTemplate<String, byte[]> byteArrayRedisTemplate;

    public RedisChallengeRepository(RedisTemplate<String, byte[]> byteArrayRedisTemplate) {
        this.byteArrayRedisTemplate = byteArrayRedisTemplate;
    }

    @Override
    public void saveChallenge(String identifier, ByteArray challenge) {
        String key = CHALLENGE_PREFIX + identifier;
        byteArrayRedisTemplate.opsForValue().set(
                key,
                challenge.getBytes(),
                CHALLENGE_TTL_MINUTES,
                TimeUnit.MINUTES
        );
    }

    @Override
    public Optional<ByteArray> getChallenge(String identifier) {
        String key = CHALLENGE_PREFIX + identifier;
        byte[] bytes = byteArrayRedisTemplate.opsForValue().get(key);
        return bytes != null ? Optional.of(new ByteArray(bytes)) : Optional.empty();
    }

    @Override
    public void removeChallenge(String identifier) {
        String key = CHALLENGE_PREFIX + identifier;
        byteArrayRedisTemplate.delete(key);
    }
}
