package aleosh.online.vivia.features.properties.likes.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class PropertyLikeId implements Serializable {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "property_id")
    private UUID propertyId;

    public PropertyLikeId() {}

    public PropertyLikeId(UUID userId, UUID propertyId) {
        this.userId = userId;
        this.propertyId = propertyId;
    }

    public UUID getUserId() { return userId; }
    public UUID getPropertyId() { return propertyId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyLikeId that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(propertyId, that.propertyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, propertyId);
    }
}
