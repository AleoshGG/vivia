package aleosh.online.vivia.features.properties.properties.data.dtos.response;

import aleosh.online.vivia.features.properties.amenity.data.dtos.response.AmenityResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos completos de la propiedad")
public class PropertyDetailContentDto {

    @Schema(description = "ID de la propiedad")
    private UUID id;

    @Schema(description = "Indica si la propiedad está disponible para renta")
    private boolean isAvailableToRent;

    @Schema(description = "Título de la publicación")
    private String title;

    @Schema(description = "Descripción de la propiedad")
    private String description;

    @Schema(description = "Superficie en metros cuadrados")
    private BigDecimal areaM2;

    @Schema(description = "Número de recámaras")
    private Integer bedrooms;

    @Schema(description = "Número de baños")
    private BigDecimal bathrooms;

    @Schema(description = "Lugares de estacionamiento")
    private Integer parkingSpaces;

    @Schema(description = "Año de construcción")
    private Integer constructionYear;

    @Schema(description = "Indica si la propiedad es parte de un condominio")
    private boolean isCondominium;

    @Schema(description = "Precio de renta publicado")
    private BigDecimal listedPrice;

    @Schema(description = "Precio por metro cuadrado")
    private BigDecimal pricePerM2;

    @Schema(description = "Fecha de creación del registro")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime updatedAt;

    @Schema(description = "Tipo de propiedad")
    private PropertyDetailPropertyTypeDto propertyType;

    @Schema(description = "Dirección")
    private PropertyDetailAddressDto address;

    @Schema(description = "Amenidades disponibles")
    private List<AmenityResponseDto> amenities;

    @Schema(description = "Indica si el usuario autenticado (LESSEE) tiene la propiedad en favoritos. Siempre false para LESSOR.")
    private boolean like;

    @Schema(description = "Datos del arrendador. Solo se incluye cuando el rol del solicitante es LESSEE; null en caso contrario.")
    private PropertyDetailLessorDto lessor;
}
