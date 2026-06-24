package aleosh.online.vivia.features.address.neighborhoods.domain.entities;

import aleosh.online.vivia.features.address.neighborhoods.domain.exceptions.InvalidNeighborhoodException;
import java.util.UUID;
import java.util.regex.Pattern;

public class Neighborhood {
    private final UUID id;
    private final String name;
    private final String postalCode;

    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^\\d{5}$");

    private Neighborhood(Builder builder) {
        validate(builder);
        this.id = builder.id;
        this.name = builder.name;
        this.postalCode = builder.postalCode;
    }

    private void validate(Builder builder) {
        if (builder.id == null) {
            throw new InvalidNeighborhoodException("Neighborhood ID is required");
        }

        if (builder.name == null || builder.name.trim().isEmpty()) {
            throw new InvalidNeighborhoodException("Neighborhood name is required");
        }

        if (builder.name.length() > 100) {
            throw new InvalidNeighborhoodException("Neighborhood name must not exceed 100 characters");
        }

        if (builder.postalCode != null && !POSTAL_CODE_PATTERN.matcher(builder.postalCode).matches()) {
            throw new InvalidNeighborhoodException("Postal code must be exactly 5 digits");
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

    public String getPostalCode() {
        return postalCode;
    }

    public static class Builder {
        private UUID id;
        private String name;
        private String postalCode;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Neighborhood build() {
            return new Neighborhood(this);
        }
    }
}
