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
@Schema(description = "Dirección de la propiedad")
public class PropertyDetailAddressDto {

    @Schema(description = "ID de la dirección")
    private UUID id;

    @Schema(description = "Calle")
    private String street;

    @Schema(description = "Número exterior")
    private String exteriorNumber;

    @Schema(description = "Número interior (puede ser nulo)")
    private String interiorNumber;

    @Schema(description = "Colonia")
    private PropertyDetailNeighborhoodDto neighborhood;
}
