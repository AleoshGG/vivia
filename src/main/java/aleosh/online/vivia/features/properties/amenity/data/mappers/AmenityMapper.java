package aleosh.online.vivia.features.properties.amenity.data.mappers;

import aleosh.online.vivia.features.properties.amenity.data.entities.AmenityEntity;
import aleosh.online.vivia.features.properties.amenity.domain.entities.Amenity;
import org.springframework.stereotype.Component;

@Component("amenityDataMapper")
public class AmenityMapper {

    public Amenity toDomain(AmenityEntity entity) {
        if (entity == null) {
            return null;
        }

        return Amenity.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public AmenityEntity toEntity(Amenity domain) {
        if (domain == null) {
            return null;
        }

        return AmenityEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }
}
