package aleosh.online.vivia.features.users.lessor.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Datos de atestación de la llave de acceso (WebAuthn)")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasskeyRegistrationDto {

    @Schema(description = "Identificador único de la credencial codificado en Base64Url")
    private String id;

    @Schema(description = "Datos del cliente (desafío, origen, etc.) codificados en Base64Url")
    private String clientDataJSON;

    @Schema(description = "Objeto de atestación que contiene la llave pública codificado en Base64Url")
    private String attestationObject;
}