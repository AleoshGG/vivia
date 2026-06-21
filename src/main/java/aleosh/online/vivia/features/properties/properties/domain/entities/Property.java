package aleosh.online.vivia.features.properties.properties.domain.entities;

import aleosh.online.vivia.features.properties.properties.domain.exceptions.InvalidPropertyException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Property {
    private final UUID id;
    private final UUID lessorId;
    private final UUID propertyTypeId;
    private final UUID addressId;
    private final boolean isAvailableToRent;

    private final BigDecimal areaM2;
    private final Integer bedrooms;
    private final BigDecimal bathrooms;
    private final Integer parkingSpaces;
    private final Integer constructionYear;
    private final boolean isCondominium;

    private final BigDecimal listedPrice;
    private final BigDecimal pricePerM2;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Property(Builder builder) {
        validate(builder);
        this.id = builder.id;
        this.lessorId = builder.lessorId;
        this.propertyTypeId = builder.propertyTypeId;
        this.addressId = builder.addressId;
        this.isAvailableToRent = builder.isAvailableToRent;
        this.areaM2 = builder.areaM2;
        this.bedrooms = builder.bedrooms;
        this.bathrooms = builder.bathrooms;
        this.parkingSpaces = builder.parkingSpaces;
        this.constructionYear = builder.constructionYear;
        this.isCondominium = builder.isCondominium;
        this.listedPrice = builder.listedPrice;
        this.pricePerM2 = builder.pricePerM2;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    private void validate(Builder builder) {
        if (builder.id == null) {
            throw new InvalidPropertyException("Property ID is required");
        }

        if (builder.lessorId == null) {
            throw new InvalidPropertyException("Lessor ID is required");
        }

        if (builder.propertyTypeId == null) {
            throw new InvalidPropertyException("Property type ID is required");
        }

        if (builder.addressId == null) {
            throw new InvalidPropertyException("Address ID is required");
        }

        if (builder.areaM2 == null || builder.areaM2.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPropertyException("Area must be greater than 0");
        }

        if (builder.bedrooms == null || builder.bedrooms < 0) {
            throw new InvalidPropertyException("Bedrooms must be 0 or greater");
        }

        if (builder.bathrooms == null || builder.bathrooms.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPropertyException("Bathrooms must be greater than 0");
        }

        if (builder.listedPrice == null || builder.listedPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPropertyException("Listed price must be greater than 0");
        }

        if (builder.pricePerM2 == null || builder.pricePerM2.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPropertyException("Price per m2 must be greater than 0");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getLessorId() { return lessorId; }
    public UUID getPropertyTypeId() { return propertyTypeId; }
    public UUID getAddressId() { return addressId; }
    public boolean isAvailableToRent() { return isAvailableToRent; }
    public BigDecimal getAreaM2() { return areaM2; }
    public Integer getBedrooms() { return bedrooms; }
    public BigDecimal getBathrooms() { return bathrooms; }
    public Integer getParkingSpaces() { return parkingSpaces; }
    public Integer getConstructionYear() { return constructionYear; }
    public boolean isCondominium() { return isCondominium; }
    public BigDecimal getListedPrice() { return listedPrice; }
    public BigDecimal getPricePerM2() { return pricePerM2; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public static class Builder {
        private UUID id;
        private UUID lessorId;
        private UUID propertyTypeId;
        private UUID addressId;
        private boolean isAvailableToRent = false;

        private BigDecimal areaM2;
        private Integer bedrooms;
        private BigDecimal bathrooms;
        private Integer parkingSpaces;
        private Integer constructionYear;
        private boolean isCondominium = false;

        private BigDecimal listedPrice;
        private BigDecimal pricePerM2;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder lessorId(UUID lessorId) { this.lessorId = lessorId; return this; }
        public Builder propertyTypeId(UUID propertyTypeId) { this.propertyTypeId = propertyTypeId; return this; }
        public Builder addressId(UUID addressId) { this.addressId = addressId; return this; }
        public Builder isAvailableToRent(boolean isAvailableToRent) { this.isAvailableToRent = isAvailableToRent; return this; }
        public Builder areaM2(BigDecimal areaM2) { this.areaM2 = areaM2; return this; }
        public Builder bedrooms(Integer bedrooms) { this.bedrooms = bedrooms; return this; }
        public Builder bathrooms(BigDecimal bathrooms) { this.bathrooms = bathrooms; return this; }
        public Builder parkingSpaces(Integer parkingSpaces) { this.parkingSpaces = parkingSpaces; return this; }
        public Builder constructionYear(Integer constructionYear) { this.constructionYear = constructionYear; return this; }
        public Builder isCondominium(boolean isCondominium) { this.isCondominium = isCondominium; return this; }
        public Builder listedPrice(BigDecimal listedPrice) { this.listedPrice = listedPrice; return this; }
        public Builder pricePerM2(BigDecimal pricePerM2) { this.pricePerM2 = pricePerM2; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Property build() {
            return new Property(this);
        }
    }
}
