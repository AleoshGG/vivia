package aleosh.online.vivia.features.properties.properties.data.dtos.response;

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
public class PropertyPreviewResponseDto {
    private UUID id;
    private String mainImageUrl;
    private String title;
    private BigDecimal listedPrice;
    private BigDecimal areaM2;
    private Integer bedrooms;
    private BigDecimal bathrooms;
    private String propertyTypeName;
}
