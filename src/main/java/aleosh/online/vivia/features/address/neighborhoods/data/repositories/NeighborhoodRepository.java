package aleosh.online.vivia.features.address.neighborhoods.data.repositories;

import aleosh.online.vivia.features.address.neighborhoods.data.entities.NeighborhoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NeighborhoodRepository extends JpaRepository<NeighborhoodEntity, UUID> {
    List<NeighborhoodEntity> findByPostalCode(String postalCode);
}
