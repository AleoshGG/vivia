package aleosh.online.vivia.features.properties.draft.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear un borrador de propiedad con carga de archivos multimedia")
public class CreatePropertyDraftRequestDto {

    @Schema(example = "550e8400-e29b-41d4-a716-446655440001", description = "ID del tipo de propiedad")
    @NotNull(message = "Property type ID is required")
    private UUID propertyTypeId;

    // Address data (embedded in draft, not stored in PostgreSQL until approved)
    @Schema(example = "8e2c08bd-640b-4c02-ae92-095e64c44709", description = "ID de la colonia")
    @NotNull(message = "Neighborhood ID is required")
    private UUID neighborhoodId;

    @Schema(example = "Real del monte", description = "Nombre de la calle")
    @NotBlank(message = "Street is required")
    @Size(max = 100, message = "Street must not exceed 100 characters")
    private String street;

    @Schema(example = "149", description = "Número exterior")
    @NotBlank(message = "Exterior number is required")
    @Size(max = 10, message = "Exterior number must not exceed 10 characters")
    private String exteriorNumber;

    @Schema(example = "N/A", description = "Número interior (opcional)")
    @Size(max = 10, message = "Interior number must not exceed 10 characters")
    private String interiorNumber;

    @Schema(example = "false", description = "Indica si la propiedad está disponible para renta inmediata")
    private Boolean isAvailableToRent = false;

    @Schema(example = "Pruebas PIPELINE 9 24-05-2026", description = "Título de la propiedad")
    @NotBlank(message = "Title is required")
    @Size(min = 10, max = 200, message = "Title must be between 10 and 200 characters")
    private String title;

    @Schema(
            example = "Amplia casa de 2 pisos con excelente ubicación, cerca de escuelas y centros comerciales. Ideal para familias.",
            description = "Descripción detallada de la propiedad"
    )
    @NotBlank(message = "Description is required")
    @Size(min = 20, max = 2000, message = "Description must be between 20 and 2000 characters")
    private String description;

    @Schema(example = "120.5", description = "Superficie en metros cuadrados")
    @NotNull(message = "Area is required")
    @DecimalMin(value = "0.01", message = "Area must be greater than zero")
    private BigDecimal areaM2;

    @Schema(example = "3", description = "Número de recámaras")
    @NotNull(message = "Number of bedrooms is required")
    @Min(value = 0, message = "Bedrooms must be at least 0")
    private Integer bedrooms;

    @Schema(example = "2.5", description = "Número de baños (acepta medios baños, ej. 1.5)")
    @NotNull(message = "Number of bathrooms is required")
    @DecimalMin(value = "0.5", message = "Bathrooms must be at least 0.5")
    private BigDecimal bathrooms;

    @Schema(example = "2", description = "Número de cajones de estacionamiento")
    @Min(value = 0, message = "Parking spaces must be at least 0")
    private Integer parkingSpaces;

    @Schema(example = "2018", description = "Año de construcción")
    @Min(value = 1900, message = "Construction year must be at least 1900")
    @Max(value = 2100, message = "Construction year must be at most 2100")
    private Integer constructionYear;

    @Schema(example = "false", description = "Indica si la propiedad es parte de un condominio")
    private Boolean isCondominium = false;

    @Schema(example = "15000.00", description = "Precio de renta mensual en MXN")
    @NotNull(message = "Listed price is required")
    @DecimalMin(value = "0.01", message = "Listed price must be greater than zero")
    private BigDecimal listedPrice;

    @Schema(description = "Lista de archivos multimedia a subir")
    @NotNull(message = "Media manifest is required")
    @NotEmpty(message = "Media manifest must contain at least one item")
    @Valid
    private List<MediaManifestItemDto> mediaManifest;
}
