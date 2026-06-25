package aleosh.online.vivia.features.properties.likes.data.repositories;

import aleosh.online.vivia.features.properties.likes.data.entities.PropertyLikeEntity;
import aleosh.online.vivia.features.properties.likes.data.entities.PropertyLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyLikeRepository extends JpaRepository<PropertyLikeEntity, PropertyLikeId> {
    boolean existsByIdUserIdAndIdPropertyId(String userId, String propertyId);
    void deleteByIdUserIdAndIdPropertyId(String userId, String propertyId);
    List<PropertyLikeEntity> findAllByIdUserId(String userId);
}
