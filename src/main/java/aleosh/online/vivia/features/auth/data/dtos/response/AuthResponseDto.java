package aleosh.online.vivia.features.auth.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Modelo de respuesta del loggeo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    @Schema(description = "Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Refresh Token para renovar sesión", example = "d9b2d63d-a233-4123-8478-...")
    private String refreshToken;
}

