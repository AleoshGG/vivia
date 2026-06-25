package aleosh.online.vivia.features.auth.data.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Modelo simple para almacenar Refresh Tokens en Redis
 * (No es una entidad JPA)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenData {
    private String userIdentifier;
    private String role;
    private Instant expiryDate;
}
