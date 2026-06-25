package aleosh.online.vivia.features.properties.likes.domain.repositories;

import aleosh.online.vivia.features.properties.likes.domain.entities.PropertyLike;

import java.util.List;
import java.util.UUID;

public interface IPropertyLikeRepository {
    PropertyLike save(PropertyLike like);
    void deleteByUserIdAndPropertyId(UUID userId, UUID propertyId);
    boolean existsByUserIdAndPropertyId(UUID userId, UUID propertyId);
    List<PropertyLike> findAllByUserId(UUID userId);
}
