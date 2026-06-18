package aleosh.online.vivia.features.auth.services.impl;

import aleosh.online.vivia.features.auth.data.entities.RefreshTokenEntity;
import aleosh.online.vivia.features.auth.data.repositories.RefreshTokenRepository;
import aleosh.online.vivia.features.auth.domain.exceptions.AuthException;
import aleosh.online.vivia.features.auth.domain.exceptions.TokenExpiredException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenDurationMs;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshTokenEntity createRefreshToken(String userIdentifier, String role) {
        refreshTokenRepository.deleteByUserIdentifier(userIdentifier);

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .userIdentifier(userIdentifier)
                .role(role)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenExpiredException("El Refresh Token expiró. Por favor, inicie sesión nuevamente.");
        }
        return token;
    }

    @Transactional
    public void deleteByUserIdentifier(String userIdentifier) {
        refreshTokenRepository.deleteByUserIdentifier(userIdentifier);
    }

    public RefreshTokenEntity findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new AuthException("Refresh Token no encontrado en la base de datos", HttpStatus.NOT_FOUND));
    }
}
