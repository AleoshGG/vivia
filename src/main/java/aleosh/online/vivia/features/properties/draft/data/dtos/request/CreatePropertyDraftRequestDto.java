package aleosh.online.vivia.features.properties.draft.data.dtos.request;

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
public class CreatePropertyDraftRequestDto {

    @NotNull(message = "Property type ID is required")
    private UUID propertyTypeId;

    // Address data (embedded in draft, not stored in PostgreSQL until approved)
    @NotNull(message = "Neighborhood ID is required")
    private UUID neighborhoodId;

    @NotBlank(message = "Street is required")
    @Size(max = 100, message = "Street must not exceed 100 characters")
    private String street;

    @NotBlank(message = "Exterior number is required")
    @Size(max = 10, message = "Exterior number must not exceed 10 characters")
    private String exteriorNumber;

    @Size(max = 10, message = "Interior number must not exceed 10 characters")
    private String interiorNumber;

    private Boolean isAvailableToRent = false;

    @NotBlank(message = "Title is required")
    @Size(min = 10, max = 200, message = "Title must be between 10 and 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 20, max = 2000, message = "Description must be between 20 and 2000 characters")
    private String description;

    @NotNull(message = "Area is required")
    @DecimalMin(value = "0.01", message = "Area must be greater than zero")
    private BigDecimal areaM2;

    @NotNull(message = "Number of bedrooms is required")
    @Min(value = 0, message = "Bedrooms must be at least 0")
    private Integer bedrooms;

    @NotNull(message = "Number of bathrooms is required")
    @DecimalMin(value = "0.5", message = "Bathrooms must be at least 0.5")
    private BigDecimal bathrooms;

    @Min(value = 0, message = "Parking spaces must be at least 0")
    private Integer parkingSpaces;

    @Min(value = 1900, message = "Construction year must be at least 1900")
    @Max(value = 2100, message = "Construction year must be at most 2100")
    private Integer constructionYear;

    private Boolean isCondominium = false;

    @NotNull(message = "Listed price is required")
    @DecimalMin(value = "0.01", message = "Listed price must be greater than zero")
    private BigDecimal listedPrice;

    @NotNull(message = "Media manifest is required")
    @NotEmpty(message = "Media manifest must contain at least one item")
    @Valid
    private List<MediaManifestItemDto> mediaManifest;
}
