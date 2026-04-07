package aleosh.online.vivia.features.properties.address.data.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Modelo para la dirección de una propiedad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {

    @Schema(description = "Dirección completa (calle y número)", example = "Av. Siempre Viva 123")
    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @Schema(description = "Ciudad", example = "Puebla")
    @NotBlank(message = "La ciudad es obligatoria")
    private String city;

    @Schema(description = "Estado", example = "Puebla")
    @NotBlank(message = "El estado es obligatorio")
    private String state;

    @Schema(description = "Colonia o barrio", example = "Centro Histórico")
    @NotBlank(message = "La colonia o barrio es obligatorio")
    private String neighborhood;
}
