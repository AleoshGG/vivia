package aleosh.online.vivia.features.users.lessor.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Respuesta de verificación de credencial biométrica para arrendador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterLessorBiometricVerifyDto {

    @NotBlank(message = "La respuesta de la credencial es requerida")
    @Schema(description = "JSON de respuesta del dispositivo con la credencial WebAuthn")
    private String credentialResponseJson;
}
