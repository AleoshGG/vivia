package aleosh.online.vivia.features.properties.likes.domain.entities;

import aleosh.online.vivia.features.properties.likes.domain.exceptions.InvalidPropertyLikeException;

import java.time.OffsetDateTime;
import java.util.UUID;

public class PropertyLike {
    private final UUID userId;
    private final UUID propertyId;
    private final OffsetDateTime createdAt;

    private PropertyLike(Builder builder) {
        validate(builder);
        this.userId = builder.userId;
        this.propertyId = builder.propertyId;
        this.createdAt = builder.createdAt;
    }

    private void validate(Builder builder) {
        if (builder.userId == null) {
            throw new InvalidPropertyLikeException("User ID es requerido");
        }
        if (builder.propertyId == null) {
            throw new InvalidPropertyLikeException("Property ID es requerido");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getUserId() { return userId; }
    public UUID getPropertyId() { return propertyId; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    public static class Builder {
        private UUID userId;
        private UUID propertyId;
        private OffsetDateTime createdAt;

        public Builder userId(UUID userId) { this.userId = userId; return this; }
        public Builder propertyId(UUID propertyId) { this.propertyId = propertyId; return this; }
        public Builder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }

        public PropertyLike build() { return new PropertyLike(this); }
    }
}
