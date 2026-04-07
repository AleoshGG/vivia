package aleosh.online.vivia.features.auth.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Modelo para inicio de sesión tradicional")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    @NotBlank(message = "El identificador (correo o nombre de empresa) es obligatorio")
    @Schema(description = "Correo (Arrendatario) o Nombre de empresa (Arrendador)", example = "usuario@example.com")
    private String identifier;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña", example = "password123")
    private String password;
}
