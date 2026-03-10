package aleosh.online.vivia.features.users.lessee.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(description = "Respuesta con la información pública del arrendatario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LesseeResponseDto {
    @Schema(description = "ID del arrendatario", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private UUID id;

    @Schema(description = "Nombre de usuario", example = "john_doe")
    private String username;

    @Schema(description = "Correo electrónico", example = "john.doe@example.com")
    private String email;
}