package aleosh.online.vivia.features.auth.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Modelo para la petición de loggeo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDto {
    @Schema(description = "Nombre de usuario", example = "dev_master_99")
    private String username;

    @Schema(description = "Contraseña del usuario", example = "pass234")
    private String password;
}
