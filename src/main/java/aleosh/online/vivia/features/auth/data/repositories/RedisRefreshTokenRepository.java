package aleosh.online.vivia.features.auth.data.repositories;

import aleosh.online.vivia.features.auth.data.models.RefreshTokenData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Repositorio para gestionar Refresh Tokens en Redis
 */
@Repository
public class RedisRefreshTokenRepository {

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String USER_TOKEN_PREFIX = "user_refresh_token:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final long refreshTokenDurationMs;

    public RedisRefreshTokenRepository(
            RedisTemplate<String, Object> redisTemplate,
            @Value("${jwt.refresh.expiration}") Long refreshTokenDurationMs
    ) {
        this.redisTemplate = redisTemplate;
        this.refreshTokenDurationMs = refreshTokenDurationMs;
    }

    /**
     * Guarda un refresh token en Redis con TTL automático
     */
    public void save(String token, RefreshTokenData data) {
        String tokenKey = REFRESH_TOKEN_PREFIX + token;
        String userKey = USER_TOKEN_PREFIX + data.getUserIdentifier();

        // Guardar token con TTL automático
        redisTemplate.opsForValue().set(
                tokenKey,
                data,
                refreshTokenDurationMs,
                TimeUnit.MILLISECONDS
        );

        // Guardar índice user -> token para token rotation
        redisTemplate.opsForValue().set(
                userKey,
                token,
                refreshTokenDurationMs,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * Busca un refresh token por su valor
     */
    public Optional<RefreshTokenData> findByToken(String token) {
        String key = REFRESH_TOKEN_PREFIX + token;
        RefreshTokenData data = (RefreshTokenData) redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(data);
    }

    /**
     * Elimina el refresh token de un usuario (usado para token rotation)
     */
    public void deleteByUserIdentifier(String userIdentifier) {
        String userKey = USER_TOKEN_PREFIX + userIdentifier;
        String oldToken = (String) redisTemplate.opsForValue().get(userKey);

        if (oldToken != null) {
            String tokenKey = REFRESH_TOKEN_PREFIX + oldToken;
            redisTemplate.delete(tokenKey);
        }
        redisTemplate.delete(userKey);
    }

    /**
     * Elimina un refresh token específico
     */
    public void delete(String token) {
        String key = REFRESH_TOKEN_PREFIX + token;
        redisTemplate.delete(key);
    }
}
