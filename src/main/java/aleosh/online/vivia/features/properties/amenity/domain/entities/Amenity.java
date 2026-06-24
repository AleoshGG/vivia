package aleosh.online.vivia.features.properties.amenity.domain.entities;

import aleosh.online.vivia.features.properties.amenity.domain.exceptions.InvalidAmenityException;
import java.util.UUID;

public class Amenity {
    private final UUID id;
    private final String name;

    private Amenity(Builder builder) {
        validate(builder);
        this.id = builder.id;
        this.name = builder.name;
    }

    private void validate(Builder builder) {
        if (builder.id == null) {
            throw new InvalidAmenityException("Amenity ID is required");
        }

        if (builder.name == null || builder.name.trim().isEmpty()) {
            throw new InvalidAmenityException("Amenity name is required");
        }

        if (builder.name.length() > 80) {
            throw new InvalidAmenityException("Amenity name must not exceed 80 characters");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static class Builder {
        private UUID id;
        private String name;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Amenity build() {
            return new Amenity(this);
        }
    }
}
