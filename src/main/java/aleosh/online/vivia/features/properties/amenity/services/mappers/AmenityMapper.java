package aleosh.online.vivia.features.properties.amenity.services.mappers;

import aleosh.online.vivia.features.properties.amenity.data.dtos.response.AmenityResponseDto;
import aleosh.online.vivia.features.properties.amenity.domain.entities.Amenity;
import org.springframework.stereotype.Component;

@Component("amenityServiceMapper")
public class AmenityMapper {

    public AmenityResponseDto toResponseDto(Amenity amenity) {
        if (amenity == null) {
            return null;
        }

        return AmenityResponseDto.builder()
                .id(amenity.getId())
                .name(amenity.getName())
                .build();
    }
}
