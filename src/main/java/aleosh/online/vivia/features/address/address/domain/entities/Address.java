package aleosh.online.vivia.features.address.address.domain.entities;

import aleosh.online.vivia.features.address.address.domain.exceptions.InvalidAddressException;
import java.util.UUID;

public class Address {
    private final UUID id;
    private final UUID neighborhoodId;
    private final String street;
    private final String exteriorNumber;
    private final String interiorNumber;

    private Address(Builder builder) {
        validate(builder);
        this.id = builder.id;
        this.neighborhoodId = builder.neighborhoodId;
        this.street = builder.street;
        this.exteriorNumber = builder.exteriorNumber;
        this.interiorNumber = builder.interiorNumber;
    }

    private void validate(Builder builder) {
        if (builder.id == null) {
            throw new InvalidAddressException("Address ID is required");
        }

        if (builder.neighborhoodId == null) {
            throw new InvalidAddressException("Neighborhood ID is required");
        }

        if (builder.street == null || builder.street.trim().isEmpty()) {
            throw new InvalidAddressException("Street is required");
        }

        if (builder.street.length() > 100) {
            throw new InvalidAddressException("Street must not exceed 100 characters");
        }

        if (builder.exteriorNumber == null || builder.exteriorNumber.trim().isEmpty()) {
            throw new InvalidAddressException("Exterior number is required");
        }

        if (builder.exteriorNumber.length() > 10) {
            throw new InvalidAddressException("Exterior number must not exceed 10 characters");
        }

        if (builder.interiorNumber != null && builder.interiorNumber.length() > 10) {
            throw new InvalidAddressException("Interior number must not exceed 10 characters");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public UUID getNeighborhoodId() {
        return neighborhoodId;
    }

    public String getStreet() {
        return street;
    }

    public String getExteriorNumber() {
        return exteriorNumber;
    }

    public String getInteriorNumber() {
        return interiorNumber;
    }

    public static class Builder {
        private UUID id;
        private UUID neighborhoodId;
        private String street;
        private String exteriorNumber;
        private String interiorNumber;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder neighborhoodId(UUID neighborhoodId) {
            this.neighborhoodId = neighborhoodId;
            return this;
        }

        public Builder street(String street) {
            this.street = street;
            return this;
        }

        public Builder exteriorNumber(String exteriorNumber) {
            this.exteriorNumber = exteriorNumber;
            return this;
        }

        public Builder interiorNumber(String interiorNumber) {
            this.interiorNumber = interiorNumber;
            return this;
        }

        public Address build() {
            return new Address(this);
        }
    }
}
