package aleosh.online.vivia.features.address.address.data.mappers;

import aleosh.online.vivia.features.address.address.data.entities.AddressEntity;
import aleosh.online.vivia.features.address.address.domain.entities.Address;
import aleosh.online.vivia.features.address.neighborhoods.data.entities.NeighborhoodEntity;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("addressDataMapper")
public class AddressMapper {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    public Address toDomain(AddressEntity entity) {
        if (entity == null) {
            return null;
        }

        BigDecimal latitude = null;
        BigDecimal longitude = null;
        if (entity.getLocation() != null) {
            // JTS: x = longitud, y = latitud
            latitude = BigDecimal.valueOf(entity.getLocation().getY());
            longitude = BigDecimal.valueOf(entity.getLocation().getX());
        }

        return Address.builder()
                .id(entity.getId())
                .neighborhoodId(entity.getNeighborhood() != null ? entity.getNeighborhood().getId() : null)
                .street(entity.getStreet())
                .exteriorNumber(entity.getExteriorNumber())
                .interiorNumber(entity.getInteriorNumber())
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    public AddressEntity toEntity(Address domain) {
        if (domain == null) {
            return null;
        }

        NeighborhoodEntity neighborhoodEntity = new NeighborhoodEntity();
        neighborhoodEntity.setId(domain.getNeighborhoodId());

        Point location = toPoint(domain.getLatitude(), domain.getLongitude());

        return AddressEntity.builder()
                .id(domain.getId())
                .neighborhood(neighborhoodEntity)
                .street(domain.getStreet())
                .exteriorNumber(domain.getExteriorNumber())
                .interiorNumber(domain.getInteriorNumber())
                .location(location)
                .build();
    }

    public Point toPoint(BigDecimal latitude, BigDecimal longitude) {
        if (latitude == null || longitude == null) {
            return null;
        }
        // JTS: x = longitud, y = latitud
        return GEOMETRY_FACTORY.createPoint(new Coordinate(longitude.doubleValue(), latitude.doubleValue()));
    }
}
