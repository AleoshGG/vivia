package aleosh.online.vivia.features.properties.properties.domain.entities;

import aleosh.online.vivia.features.properties.properties.domain.exceptions.InvalidPropertyTypeException;
import java.util.UUID;

public class PropertyType {
    private final UUID id;
    private final String name;

    private PropertyType(Builder builder) {
        validate(builder);
        this.id = builder.id;
        this.name = builder.name;
    }

    private void validate(Builder builder) {
        if (builder.id == null) {
            throw new InvalidPropertyTypeException("Property type ID is required");
        }

        if (builder.name == null || builder.name.trim().isEmpty()) {
            throw new InvalidPropertyTypeException("Property type name is required");
        }

        if (builder.name.length() > 50) {
            throw new InvalidPropertyTypeException("Property type name must not exceed 50 characters");
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

        public PropertyType build() {
            return new PropertyType(this);
        }
    }
}
