package aleosh.online.vivia.features.properties.draft.domain.entities;

import aleosh.online.vivia.features.properties.draft.domain.exceptions.InvalidPropertyDraftException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@JsonDeserialize(builder = PropertyDraft.Builder.class)
public class PropertyDraft {

    private final UUID id;
    private final UUID lessorId;
    private final PropertyTypeData propertyType;
    private final AddressData address;
    private final Map<String, PropertyDraftMedia> mediaFiles;
    private final boolean isAvailableToRent;
    private final String title;
    private final String description;
    private final BigDecimal areaM2;
    private final Integer bedrooms;
    private final BigDecimal bathrooms;
    private final Integer parkingSpaces;
    private final Integer constructionYear;
    private final boolean isCondominium;
    private final BigDecimal listedPrice;
    private final BigDecimal pricePerM2;
    private final String status;
    private final int totalFiles;
    private final int totalImages;
    private final int totalVideos;
    private final int approvedFiles;
    private final int rejectedFiles;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Instant expiresAt;

    private PropertyDraft(Builder builder) {
        this.id = builder.id;
        this.lessorId = builder.lessorId;
        this.propertyType = builder.propertyType;
        this.address = builder.address;
        this.mediaFiles = builder.mediaFiles;
        this.isAvailableToRent = builder.isAvailableToRent;
        this.title = builder.title;
        this.description = builder.description;
        this.areaM2 = builder.areaM2;
        this.bedrooms = builder.bedrooms;
        this.bathrooms = builder.bathrooms;
        this.parkingSpaces = builder.parkingSpaces;
        this.constructionYear = builder.constructionYear;
        this.isCondominium = builder.isCondominium;
        this.listedPrice = builder.listedPrice;
        this.pricePerM2 = builder.pricePerM2;
        this.status = builder.status;
        this.totalFiles = builder.totalFiles;
        this.totalImages = builder.totalImages;
        this.totalVideos = builder.totalVideos;
        this.approvedFiles = builder.approvedFiles;
        this.rejectedFiles = builder.rejectedFiles;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.expiresAt = builder.expiresAt;

        validate();
    }

    private void validate() {
        if (id == null) {
            throw new InvalidPropertyDraftException("Property ID is required");
        }
        if (lessorId == null) {
            throw new InvalidPropertyDraftException("Lessor ID is required");
        }
        if (title == null || title.isBlank()) {
            throw new InvalidPropertyDraftException("Title is required");
        }
        if (listedPrice == null || listedPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPropertyDraftException("Listed price must be greater than zero");
        }
        if (address == null) {
            throw new InvalidPropertyDraftException("Address is required");
        }
        if (propertyType == null) {
            throw new InvalidPropertyDraftException("Property type is required");
        }
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getLessorId() {
        return lessorId;
    }

    public PropertyTypeData getPropertyType() {
        return propertyType;
    }

    public AddressData getAddress() {
        return address;
    }

    public Map<String, PropertyDraftMedia> getMediaFiles() {
        return mediaFiles;
    }

    public boolean isAvailableToRent() {
        return isAvailableToRent;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAreaM2() {
        return areaM2;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public BigDecimal getBathrooms() {
        return bathrooms;
    }

    public Integer getParkingSpaces() {
        return parkingSpaces;
    }

    public Integer getConstructionYear() {
        return constructionYear;
    }

    public boolean isCondominium() {
        return isCondominium;
    }

    public BigDecimal getListedPrice() {
        return listedPrice;
    }

    public BigDecimal getPricePerM2() {
        return pricePerM2;
    }

    public String getStatus() {
        return status;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public int getTotalImages() {
        return totalImages;
    }

    public int getTotalVideos() {
        return totalVideos;
    }

    public int getApprovedFiles() {
        return approvedFiles;
    }

    public int getRejectedFiles() {
        return rejectedFiles;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private UUID id;
        private UUID lessorId;
        private PropertyTypeData propertyType;
        private AddressData address;
        private Map<String, PropertyDraftMedia> mediaFiles;
        private boolean isAvailableToRent;
        private String title;
        private String description;
        private BigDecimal areaM2;
        private Integer bedrooms;
        private BigDecimal bathrooms;
        private Integer parkingSpaces;
        private Integer constructionYear;
        private boolean isCondominium;
        private BigDecimal listedPrice;
        private BigDecimal pricePerM2;
        private String status;
        private int totalFiles;
        private int totalImages;
        private int totalVideos;
        private int approvedFiles;
        private int rejectedFiles;
        private Instant createdAt;
        private Instant updatedAt;
        private Instant expiresAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder lessorId(UUID lessorId) {
            this.lessorId = lessorId;
            return this;
        }

        public Builder propertyType(PropertyTypeData propertyType) {
            this.propertyType = propertyType;
            return this;
        }

        public Builder address(AddressData address) {
            this.address = address;
            return this;
        }

        public Builder mediaFiles(Map<String, PropertyDraftMedia> mediaFiles) {
            this.mediaFiles = mediaFiles;
            return this;
        }

        public Builder isAvailableToRent(boolean isAvailableToRent) {
            this.isAvailableToRent = isAvailableToRent;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder areaM2(BigDecimal areaM2) {
            this.areaM2 = areaM2;
            return this;
        }

        public Builder bedrooms(Integer bedrooms) {
            this.bedrooms = bedrooms;
            return this;
        }

        public Builder bathrooms(BigDecimal bathrooms) {
            this.bathrooms = bathrooms;
            return this;
        }

        public Builder parkingSpaces(Integer parkingSpaces) {
            this.parkingSpaces = parkingSpaces;
            return this;
        }

        public Builder constructionYear(Integer constructionYear) {
            this.constructionYear = constructionYear;
            return this;
        }

        public Builder isCondominium(boolean isCondominium) {
            this.isCondominium = isCondominium;
            return this;
        }

        public Builder listedPrice(BigDecimal listedPrice) {
            this.listedPrice = listedPrice;
            return this;
        }

        public Builder pricePerM2(BigDecimal pricePerM2) {
            this.pricePerM2 = pricePerM2;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder totalFiles(int totalFiles) {
            this.totalFiles = totalFiles;
            return this;
        }

        public Builder totalImages(int totalImages) {
            this.totalImages = totalImages;
            return this;
        }

        public Builder totalVideos(int totalVideos) {
            this.totalVideos = totalVideos;
            return this;
        }

        public Builder approvedFiles(int approvedFiles) {
            this.approvedFiles = approvedFiles;
            return this;
        }

        public Builder rejectedFiles(int rejectedFiles) {
            this.rejectedFiles = rejectedFiles;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder expiresAt(Instant expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public PropertyDraft build() {
            return new PropertyDraft(this);
        }
    }

    // Inner class for PropertyType data
    public static class PropertyTypeData {
        private final UUID id;
        private final String name;

        @com.fasterxml.jackson.annotation.JsonCreator
        public PropertyTypeData(
                @com.fasterxml.jackson.annotation.JsonProperty("id") UUID id,
                @com.fasterxml.jackson.annotation.JsonProperty("name") String name
        ) {
            this.id = id;
            this.name = name;
        }

        public UUID getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    // Inner class for Address data
    public static class AddressData {
        private final UUID id;
        private final UUID neighborhoodId;
        private final String neighborhoodName;
        private final String postalCode;
        private final String street;
        private final String exteriorNumber;
        private final String interiorNumber;

        @com.fasterxml.jackson.annotation.JsonCreator
        public AddressData(
                @com.fasterxml.jackson.annotation.JsonProperty("id") UUID id,
                @com.fasterxml.jackson.annotation.JsonProperty("neighborhoodId") UUID neighborhoodId,
                @com.fasterxml.jackson.annotation.JsonProperty("neighborhoodName") String neighborhoodName,
                @com.fasterxml.jackson.annotation.JsonProperty("postalCode") String postalCode,
                @com.fasterxml.jackson.annotation.JsonProperty("street") String street,
                @com.fasterxml.jackson.annotation.JsonProperty("exteriorNumber") String exteriorNumber,
                @com.fasterxml.jackson.annotation.JsonProperty("interiorNumber") String interiorNumber
        ) {
            this.id = id;
            this.neighborhoodId = neighborhoodId;
            this.neighborhoodName = neighborhoodName;
            this.postalCode = postalCode;
            this.street = street;
            this.exteriorNumber = exteriorNumber;
            this.interiorNumber = interiorNumber;
        }

        public UUID getId() {
            return id;
        }

        public UUID getNeighborhoodId() {
            return neighborhoodId;
        }

        public String getNeighborhoodName() {
            return neighborhoodName;
        }

        public String getPostalCode() {
            return postalCode;
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
    }
}
