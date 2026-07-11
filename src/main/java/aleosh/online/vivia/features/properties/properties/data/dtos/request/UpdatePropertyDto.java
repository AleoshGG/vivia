package aleosh.online.vivia.features.properties.properties.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para actualizar parcialmente una propiedad; los campos nulos u omitidos no se modifican. "
        + "Permite cambiar datos generales, tipo de propiedad, dirección y amenidades")
public class UpdatePropertyDto {

    @Size(min = 10, max = 200, message = "title debe tener entre 10 y 200 caracteres")
    @Schema(example = "Departamento remodelado cerca del metro", description = "Nuevo título de la propiedad (omitir para no cambiar)")
    private String title;

    @Size(min = 20, max = 2000, message = "description debe tener entre 20 y 2000 caracteres")
    @Schema(example = "Departamento de 2 recámaras con vista a la calle, cocina integral y estacionamiento incluido. Recién remodelado.", description = "Nueva descripción (omitir para no cambiar)")
    private String description;

    @DecimalMin(value = "0.01", message = "areaM2 debe ser mayor a cero")
    @Schema(example = "80.50", description = "Superficie en metros cuadrados (omitir para no cambiar)")
    private BigDecimal areaM2;

    @Min(value = 0, message = "bedrooms debe ser 0 o mayor")
    @Schema(example = "3", description = "Número de recámaras (omitir para no cambiar)")
    private Integer bedrooms;

    @DecimalMin(value = "0.5", message = "bathrooms debe ser mayor a cero")
    @Schema(example = "2.0", description = "Número de baños (omitir para no cambiar)")
    private BigDecimal bathrooms;

    @Min(value = 0, message = "parkingSpaces debe ser 0 o mayor")
    @Schema(example = "2", description = "Cajones de estacionamiento (omitir para no cambiar)")
    private Integer parkingSpaces;

    @Schema(example = "2012", description = "Año de construcción (omitir para no cambiar)")
    private Integer constructionYear;

    @Schema(example = "true", description = "Indica si la propiedad es condominio (omitir para no cambiar)")
    private Boolean isCondominium;

    @Schema(example = "true", description = "Indica si la propiedad está disponible para arrendar (omitir para no cambiar)")
    private Boolean isAvailableToRent;

    @DecimalMin(value = "0.01", message = "listedPrice debe ser mayor a cero")
    @Schema(example = "13500.00", description = "Precio de renta mensual en pesos (omitir para no cambiar)")
    private BigDecimal listedPrice;

    @Schema(example = "b1a2c3d4-5e6f-7890-abcd-ef1234567890", description = "ID del nuevo tipo de propiedad (omitir para no cambiar)")
    private UUID propertyTypeId;

    @Valid
    @Schema(description = "Actualización parcial de la dirección (omitir para no cambiar)")
    private UpdateAddressPropertyDto address;

    @Schema(example = "[\"a1b2c3d4-5e6f-7890-abcd-ef1234567890\"]",
            description = "Reemplaza la lista completa de amenidades; lista vacía las elimina todas (omitir para no cambiar)")
    private List<UUID> amenityIds;
}
