package aleosh.online.vivia.features.auth.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Modelo para solicitar un nuevo Access Token")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequestDto {
    @NotBlank(message = "El refresh token es obligatorio")
    @Schema(description = "Refresh Token emitido anteriormente", example = "d9b2d63d-a233-4123-8478-...")
    private String refreshToken;
}
