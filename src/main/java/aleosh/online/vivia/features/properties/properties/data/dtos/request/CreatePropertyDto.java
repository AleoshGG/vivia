package aleosh.online.vivia.features.properties.properties.data.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePropertyDto {

    @NotNull(message = "El ID del arrendador es obligatorio")
    private UUID lessorId;

    @NotNull(message = "El ID del tipo de propiedad es obligatorio")
    private UUID propertyTypeId;

    @NotNull(message = "El ID de la colonia es obligatorio")
    private UUID neighborhoodId;

    @NotBlank(message = "La calle es obligatoria")
    @Size(max = 100, message = "La calle no debe exceder 100 caracteres")
    private String street;

    @NotBlank(message = "El número exterior es obligatorio")
    @Size(max = 10, message = "El número exterior no debe exceder 10 caracteres")
    private String exteriorNumber;

    @Size(max = 10, message = "El número interior no debe exceder 10 caracteres")
    private String interiorNumber;

    private Boolean isAvailableToRent = false;

    @NotNull(message = "El área es obligatoria")
    @DecimalMin(value = "0.01", message = "El área debe ser mayor a 0")
    private BigDecimal areaM2;

    @NotNull(message = "El número de recámaras es obligatorio")
    @Min(value = 0, message = "El número de recámaras debe ser 0 o mayor")
    private Integer bedrooms;

    @NotNull(message = "El número de baños es obligatorio")
    @DecimalMin(value = "0.5", message = "El número de baños debe ser al menos 0.5")
    private BigDecimal bathrooms;

    @Min(value = 0, message = "El número de estacionamientos debe ser 0 o mayor")
    private Integer parkingSpaces;

    @Min(value = 1900, message = "El año de construcción debe ser 1900 o posterior")
    @Max(value = 2100, message = "El año de construcción debe ser 2100 o anterior")
    private Integer constructionYear;

    private Boolean isCondominium = false;

    @NotNull(message = "El precio de renta es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio de renta debe ser mayor a 0")
    private BigDecimal listedPrice;

    @NotNull(message = "El precio por m2 es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio por m2 debe ser mayor a 0")
    private BigDecimal pricePerM2;

    @Valid
    @Builder.Default
    private List<PropertyMediaDto> media = new ArrayList<>();
}
