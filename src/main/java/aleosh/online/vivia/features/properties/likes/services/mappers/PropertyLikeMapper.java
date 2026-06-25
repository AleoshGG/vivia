package aleosh.online.vivia.features.properties.likes.services.mappers;

import aleosh.online.vivia.features.properties.likes.data.entities.PropertyLikeEntity;
import aleosh.online.vivia.features.properties.likes.data.entities.PropertyLikeId;
import aleosh.online.vivia.features.properties.likes.domain.entities.PropertyLike;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyEntity;
import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class PropertyLikeMapper {

    public PropertyLike toDomain(PropertyLikeEntity entity) {
        return PropertyLike.builder()
                .userId(UUID.fromString(entity.getId().getUserId()))
                .propertyId(UUID.fromString(entity.getId().getPropertyId()))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public PropertyLikeEntity toEntity(PropertyLike domain) {
        PropertyLikeId id = new PropertyLikeId(
                domain.getUserId().toString(),
                domain.getPropertyId().toString()
        );

        UserEntity user = new UserEntity();
        user.setId(domain.getUserId());

        PropertyEntity property = new PropertyEntity();
        property.setId(domain.getPropertyId());

        PropertyLikeEntity entity = new PropertyLikeEntity();
        entity.setId(id);
        entity.setUser(user);
        entity.setProperty(property);
        entity.setCreatedAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : OffsetDateTime.now());
        return entity;
    }
}
