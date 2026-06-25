package aleosh.online.vivia.features.properties.properties.data.mappers;

import aleosh.online.vivia.features.properties.properties.data.entities.PropertyTypeEntity;
import aleosh.online.vivia.features.properties.properties.domain.entities.PropertyType;
import org.springframework.stereotype.Component;

@Component("propertyTypeDataMapper")
public class PropertyTypeMapper {

    public PropertyType toDomain(PropertyTypeEntity entity) {
        if (entity == null) {
            return null;
        }

        return PropertyType.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public PropertyTypeEntity toEntity(PropertyType domain) {
        if (domain == null) {
            return null;
        }

        return PropertyTypeEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }
}
