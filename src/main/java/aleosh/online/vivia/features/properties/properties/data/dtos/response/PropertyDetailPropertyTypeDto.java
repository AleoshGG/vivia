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
@Schema(description = "Tipo de propiedad")
public class PropertyDetailPropertyTypeDto {

    @Schema(description = "ID del tipo de propiedad")
    private UUID id;

    @Schema(description = "Nombre del tipo (ej. Departamento, Casa)")
    private String name;
}
