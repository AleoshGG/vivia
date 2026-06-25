package aleosh.online.vivia.features.properties.properties.data.repositories;

import aleosh.online.vivia.features.properties.properties.data.entities.PropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<PropertyEntity, UUID> {
    Optional<PropertyEntity> findByLessorId(UUID lessorId);
    List<PropertyEntity> findAllByLessorId(UUID lessorId);
}
