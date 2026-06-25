package aleosh.online.vivia.features.address.address.data.mappers;

import aleosh.online.vivia.features.address.address.data.entities.AddressEntity;
import aleosh.online.vivia.features.address.address.domain.entities.Address;
import aleosh.online.vivia.features.address.neighborhoods.data.entities.NeighborhoodEntity;
import org.springframework.stereotype.Component;

@Component("addressDataMapper")
public class AddressMapper {

    public Address toDomain(AddressEntity entity) {
        if (entity == null) {
            return null;
        }

        return Address.builder()
                .id(entity.getId())
                .neighborhoodId(entity.getNeighborhood() != null ? entity.getNeighborhood().getId() : null)
                .street(entity.getStreet())
                .exteriorNumber(entity.getExteriorNumber())
                .interiorNumber(entity.getInteriorNumber())
                .build();
    }

    public AddressEntity toEntity(Address domain) {
        if (domain == null) {
            return null;
        }

        NeighborhoodEntity neighborhoodEntity = new NeighborhoodEntity();
        neighborhoodEntity.setId(domain.getNeighborhoodId());

        return AddressEntity.builder()
                .id(domain.getId())
                .neighborhood(neighborhoodEntity)
                .street(domain.getStreet())
                .exteriorNumber(domain.getExteriorNumber())
                .interiorNumber(domain.getInteriorNumber())
                .build();
    }
}
