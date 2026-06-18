package aleosh.online.vivia.features.auth.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Modelo para inicio de sesión con cuenta Google")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleLoginRequestDto {

    @NotBlank(message = "El ID Token de Google es requerido")
    @Schema(description = "ID de Google", example = "<ID TOKEN>")
    private String idToken;

    @NotBlank(message = "Rol que desempeñará el usuario es requerido")
    @Schema(description = "Rol del usuario", example = "ROLE_LESSOR / ROLE_LESSEE")
    private String role;
}
