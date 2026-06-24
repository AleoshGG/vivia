package aleosh.online.vivia.features.auth.services.impl;

import aleosh.online.vivia.features.auth.data.models.RefreshTokenData;
import aleosh.online.vivia.features.auth.data.repositories.RedisRefreshTokenRepository;
import aleosh.online.vivia.features.auth.domain.exceptions.AuthException;
import aleosh.online.vivia.features.auth.domain.exceptions.TokenExpiredException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl {

    private final RedisRefreshTokenRepository redisRefreshTokenRepository;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenDurationMs;

    public RefreshTokenServiceImpl(RedisRefreshTokenRepository redisRefreshTokenRepository) {
        this.redisRefreshTokenRepository = redisRefreshTokenRepository;
    }

    public String createRefreshToken(String userIdentifier, String role) {
        // Token rotation: eliminar token anterior del usuario
        redisRefreshTokenRepository.deleteByUserIdentifier(userIdentifier);

        // Generar nuevo token
        String token = UUID.randomUUID().toString();

        RefreshTokenData data = RefreshTokenData.builder()
                .userIdentifier(userIdentifier)
                .role(role)
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        redisRefreshTokenRepository.save(token, data);

        return token;  // Retorna el token directamente
    }

    public RefreshTokenData verifyExpiration(RefreshTokenData tokenData, String token) {
        if (tokenData.getExpiryDate().compareTo(Instant.now()) < 0) {
            redisRefreshTokenRepository.delete(token);
            throw new TokenExpiredException("El Refresh Token expiró. Por favor, inicie sesión nuevamente.");
        }
        return tokenData;
    }

    public void deleteByUserIdentifier(String userIdentifier) {
        redisRefreshTokenRepository.deleteByUserIdentifier(userIdentifier);
    }

    public RefreshTokenData findByToken(String token) {
        return redisRefreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new AuthException("Refresh Token no encontrado", HttpStatus.NOT_FOUND));
    }
}
