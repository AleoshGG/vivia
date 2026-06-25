package aleosh.online.vivia.features.properties.properties.data.dtos.response;

import java.math.BigDecimal;
import java.util.UUID;

public record PropertyPreviewResponseDto(
        UUID id,
        String mainImageUrl,
        String title,
        BigDecimal listedPrice,
        BigDecimal areaM2,
        Integer bedrooms,
        BigDecimal bathrooms,
        String propertyTypeName
) {}
