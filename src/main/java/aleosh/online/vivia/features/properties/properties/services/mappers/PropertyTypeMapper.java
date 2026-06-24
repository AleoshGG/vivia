package aleosh.online.vivia.features.properties.properties.services.mappers;

import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyTypeResponseDto;
import aleosh.online.vivia.features.properties.properties.domain.entities.PropertyType;
import org.springframework.stereotype.Component;

@Component("propertyTypeServiceMapper")
public class PropertyTypeMapper {

    public PropertyTypeResponseDto toResponseDto(PropertyType propertyType) {
        if (propertyType == null) {
            return null;
        }

        return PropertyTypeResponseDto.builder()
                .id(propertyType.getId())
                .name(propertyType.getName())
                .build();
    }
}
