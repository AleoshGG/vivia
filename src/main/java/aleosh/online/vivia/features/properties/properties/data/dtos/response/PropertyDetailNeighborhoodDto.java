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
@Schema(description = "Colonia donde se ubica la propiedad")
public class PropertyDetailNeighborhoodDto {

    @Schema(description = "ID de la colonia")
    private UUID id;

    @Schema(description = "Nombre de la colonia")
    private String name;

    @Schema(description = "Código postal")
    private String postalCode;
}
