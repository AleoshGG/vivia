package aleosh.online.vivia.features.properties.properties.data.repositories;

import aleosh.online.vivia.features.properties.properties.data.entities.PropertyMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PropertyMediaRepository extends JpaRepository<PropertyMediaEntity, UUID> {
    List<PropertyMediaEntity> findAllByProperty_Id(UUID propertyId);
}
