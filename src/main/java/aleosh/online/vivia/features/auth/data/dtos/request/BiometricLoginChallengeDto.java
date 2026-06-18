package aleosh.online.vivia.features.auth.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Modelo para solicitar challenge de login biométrico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BiometricLoginChallengeDto {
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Schema(description = "Correo electrónico del usuario", example = "usuario@example.com")
    private String email;
}
