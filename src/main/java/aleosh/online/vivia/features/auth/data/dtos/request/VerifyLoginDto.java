package aleosh.online.vivia.features.auth.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyLoginDto {
    @NotBlank(message = "La respuesta de la credencial es obligatoria")
    @Schema(description = "JSON crudo generado por el Authenticator del dispositivo móvil tras leer la huella")
    private String credentialResponseJson;
}