package aleosh.online.vivia.features.auth.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Datos para actualizar la contraseña del usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequestDto {

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Schema(description = "Nueva contraseña", example = "NuevaContraseña123")
    private String password;
}
