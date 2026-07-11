package aleosh.online.vivia.features.properties.properties.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Actualización parcial de la dirección de una propiedad; los campos nulos u omitidos no se modifican. latitude y longitude deben enviarse juntas")
public class UpdateAddressPropertyDto {

    @Schema(example = "c7e2a9f1-3d45-6789-bcde-f01234567890", description = "ID de la nueva colonia (omitir para no cambiar)")
    private UUID neighborhoodId;

    @Size(min = 1, max = 100, message = "street debe tener entre 1 y 100 caracteres")
    @Schema(example = "Av. Insurgentes Sur", description = "Nueva calle (omitir para no cambiar)")
    private String street;

    @Size(min = 1, max = 10, message = "exteriorNumber debe tener entre 1 y 10 caracteres")
    @Schema(example = "1457", description = "Nuevo número exterior (omitir para no cambiar)")
    private String exteriorNumber;

    @Size(max = 10, message = "interiorNumber no debe exceder 10 caracteres")
    @Schema(example = "4B", description = "Nuevo número interior (omitir para no cambiar)")
    private String interiorNumber;

    @DecimalMin(value = "-90", message = "latitude debe ser mayor o igual a -90")
    @DecimalMax(value = "90", message = "latitude debe ser menor o igual a 90")
    @Schema(example = "19.372850", description = "Nueva latitud; debe enviarse junto con longitude (omitir para no cambiar)")
    private BigDecimal latitude;

    @DecimalMin(value = "-180", message = "longitude debe ser mayor o igual a -180")
    @DecimalMax(value = "180", message = "longitude debe ser menor o igual a 180")
    @Schema(example = "-99.179615", description = "Nueva longitud; debe enviarse junto con latitude (omitir para no cambiar)")
    private BigDecimal longitude;
}
