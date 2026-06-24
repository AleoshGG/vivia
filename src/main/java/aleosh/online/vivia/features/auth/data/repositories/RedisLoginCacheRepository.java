package aleosh.online.vivia.features.auth.data.repositories;

import com.yubico.webauthn.AssertionRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Repositorio para almacenar AssertionRequest (login cache) en Redis
 * Reemplaza el ConcurrentHashMap en memoria para soportar múltiples instancias
 */
@Repository
public class RedisLoginCacheRepository {

    private static final String LOGIN_CACHE_PREFIX = "webauthn:challenge:login:";
    private static final long CACHE_TTL_MINUTES = 5;

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisLoginCacheRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Guarda un AssertionRequest en Redis con TTL de 5 minutos
     */
    public void save(String challengeId, AssertionRequest request) {
        String key = LOGIN_CACHE_PREFIX + challengeId;
        redisTemplate.opsForValue().set(
                key,
                request,
                CACHE_TTL_MINUTES,
                TimeUnit.MINUTES
        );
    }

    /**
     * Busca un AssertionRequest por challengeId
     */
    public Optional<AssertionRequest> find(String challengeId) {
        String key = LOGIN_CACHE_PREFIX + challengeId;
        AssertionRequest request = (AssertionRequest) redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(request);
    }

    /**
     * Elimina un AssertionRequest del caché
     */
    public void remove(String challengeId) {
        String key = LOGIN_CACHE_PREFIX + challengeId;
        redisTemplate.delete(key);
    }
}
