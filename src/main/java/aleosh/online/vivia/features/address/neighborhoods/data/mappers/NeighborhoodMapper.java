package aleosh.online.vivia.features.address.neighborhoods.data.mappers;

import aleosh.online.vivia.features.address.neighborhoods.data.entities.NeighborhoodEntity;
import aleosh.online.vivia.features.address.neighborhoods.domain.entities.Neighborhood;
import org.springframework.stereotype.Component;

@Component("neighborhoodDataMapper")
public class NeighborhoodMapper {

    public Neighborhood toDomain(NeighborhoodEntity entity) {
        if (entity == null) {
            return null;
        }

        return Neighborhood.builder()
                .id(entity.getId())
                .name(entity.getName())
                .postalCode(entity.getPostalCode())
                .build();
    }

    public NeighborhoodEntity toEntity(Neighborhood domain) {
        if (domain == null) {
            return null;
        }

        return NeighborhoodEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .postalCode(domain.getPostalCode())
                .build();
    }
}
