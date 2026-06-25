package aleosh.online.vivia.features.properties.amenity.data.dtos.response;

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
public class AmenityResponseDto {

    @Schema(description = "ID de la amenidad", example = "550e8400-e29b-41d4-a716-446655440010")
    private UUID id;

    @Schema(description = "Nombre de la amenidad", example = "Estacionamiento")
    private String name;
}
