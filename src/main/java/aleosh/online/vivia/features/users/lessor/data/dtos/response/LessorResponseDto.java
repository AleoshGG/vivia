package aleosh.online.vivia.features.users.lessor.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(description = "Respuesta con la información pública del arrendador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessorResponseDto {
    @Schema(description = "ID del arrendador", example = "a1b2c3d434e3fsdf-asdfasdf")
    private UUID id;

    @Schema(description = "Nombre del arrendador", example = "Alexis Leonel")
    private String firstName;

    @Schema(description = "Apellidos del arrendador", example = "Guzman Gonzalez")
    private String lastName;

    @Schema(description = "Nombre de la empresa", example = "Rentas Ws")
    private String companyName;
}
