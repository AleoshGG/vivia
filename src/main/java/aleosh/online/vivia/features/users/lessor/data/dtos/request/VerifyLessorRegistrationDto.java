package aleosh.online.vivia.features.users.lessor.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Modelo para verificar la credencial biométrica y finalizar el registro")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyLessorRegistrationDto {

    @NotBlank(message = "El nombre de la empresa es obligatorio para identificar el proceso")
    @Schema(description = "Nombre de la empresa del arrendador en proceso de registro", example = "Rentas Ws")
    private String companyName;

    @NotBlank(message = "La respuesta de la credencial es obligatoria")
    @Schema(description = "JSON crudo generado por el Authenticator del dispositivo móvil tras leer la huella")
    private String credentialResponseJson;
}