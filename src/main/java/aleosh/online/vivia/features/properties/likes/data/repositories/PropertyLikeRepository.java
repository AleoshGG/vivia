package aleosh.online.vivia.features.properties.likes.data.repositories;

import aleosh.online.vivia.features.properties.likes.data.entities.PropertyLikeEntity;
import aleosh.online.vivia.features.properties.likes.data.entities.PropertyLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PropertyLikeRepository extends JpaRepository<PropertyLikeEntity, PropertyLikeId> {
    boolean existsByIdUserIdAndIdPropertyId(UUID userId, UUID propertyId);
    void deleteByIdUserIdAndIdPropertyId(UUID userId, UUID propertyId);
    List<PropertyLikeEntity> findAllByIdUserId(UUID userId);
}
