package aleosh.online.vivia.features.properties.properties.data.repositories;

import aleosh.online.vivia.features.properties.properties.data.entities.PropertyTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PropertyTypeRepository extends JpaRepository<PropertyTypeEntity, UUID> {
}
