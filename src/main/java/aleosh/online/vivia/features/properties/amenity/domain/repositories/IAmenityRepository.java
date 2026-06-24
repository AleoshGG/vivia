package aleosh.online.vivia.features.properties.amenity.domain.repositories;

import aleosh.online.vivia.features.properties.amenity.domain.entities.Amenity;
import java.util.List;

public interface IAmenityRepository {
    List<Amenity> getAll();
}
