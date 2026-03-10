package aleosh.online.vivia.features.users.lessee.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Modelo para verificar la credencial biométrica y finalizar el registro del arrendatario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyLesseeRegistrationDto {

    @NotBlank(message = "El nombre de email es obligatorio para identificar el proceso")
    @Schema(description = "Nombre de email del arrendatario en proceso de registro", example = "john_doe")
    private String email;

    @NotBlank(message = "La respuesta de la credencial es obligatoria")
    @Schema(description = "JSON crudo generado por el Authenticator del dispositivo móvil tras leer la huella")
    private String credentialResponseJson;
}