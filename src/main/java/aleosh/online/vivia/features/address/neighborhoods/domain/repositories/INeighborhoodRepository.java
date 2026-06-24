package aleosh.online.vivia.features.address.neighborhoods.domain.repositories;

import aleosh.online.vivia.features.address.neighborhoods.domain.entities.Neighborhood;
import java.util.List;

public interface INeighborhoodRepository {
    List<Neighborhood> getNeighborhoodsByCP(String postalCode);
}
