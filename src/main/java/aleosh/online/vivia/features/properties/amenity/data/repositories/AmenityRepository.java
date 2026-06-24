package aleosh.online.vivia.features.properties.amenity.data.repositories;

import aleosh.online.vivia.features.properties.amenity.data.entities.AmenityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AmenityRepository extends JpaRepository<AmenityEntity, UUID> {
}
