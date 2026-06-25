package aleosh.online.vivia.features.properties.properties.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos del arrendador. Solo se incluye cuando quien consulta tiene el rol LESSEE.")
public class PropertyDetailLessorDto {

    @Schema(description = "ID del arrendador")
    private UUID id;

    @Schema(description = "Nombre(s)")
    private String name;

    @Schema(description = "Apellido paterno")
    private String paternalSurname;

    @Schema(description = "Apellido materno")
    private String maternalSurname;

    @Schema(description = "URL de la foto de perfil")
    private String photoUrl;
}
