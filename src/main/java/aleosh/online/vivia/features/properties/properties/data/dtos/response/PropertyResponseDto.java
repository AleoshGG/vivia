package aleosh.online.vivia.features.properties.properties.data.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyResponseDto {
    private UUID id;
    private UUID lessorId;
    private UUID propertyTypeId;
    private UUID addressId;
    private boolean isAvailableToRent;

    private String title;
    private String description;

    private BigDecimal areaM2;
    private Integer bedrooms;
    private BigDecimal bathrooms;
    private Integer parkingSpaces;
    private Integer constructionYear;
    private boolean isCondominium;

    private BigDecimal listedPrice;
    private BigDecimal pricePerM2;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<PropertyMediaResponseDto> media = new ArrayList<>();
}
