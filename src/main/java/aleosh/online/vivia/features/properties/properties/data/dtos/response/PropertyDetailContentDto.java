package aleosh.online.vivia.features.properties.properties.data.dtos.response;

import aleosh.online.vivia.features.properties.amenity.data.dtos.response.AmenityResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PropertyDetailContentDto {

    private final UUID id;
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
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final PropertyDetailPropertyTypeDto propertyType;
    private final PropertyDetailAddressDto address;
    private final List<AmenityResponseDto> amenities;
    private final boolean like;
    private final PropertyDetailLessorDto lessor;

    private PropertyDetailContentDto(Builder builder) {
        this.id = builder.id;
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
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.propertyType = builder.propertyType;
        this.address = builder.address;
        this.amenities = builder.amenities;
        this.like = builder.like;
        this.lessor = builder.lessor;
    }

    public static Builder builder() { return new Builder(); }

    public UUID getId() { return id; }
    public boolean isAvailableToRent() { return isAvailableToRent; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
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
    public PropertyDetailPropertyTypeDto getPropertyType() { return propertyType; }
    public PropertyDetailAddressDto getAddress() { return address; }
    public List<AmenityResponseDto> getAmenities() { return amenities; }
    public boolean isLike() { return like; }
    public PropertyDetailLessorDto getLessor() { return lessor; }

    public static class Builder {
        private UUID id;
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
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private PropertyDetailPropertyTypeDto propertyType;
        private PropertyDetailAddressDto address;
        private List<AmenityResponseDto> amenities;
        private boolean like;
        private PropertyDetailLessorDto lessor;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder isAvailableToRent(boolean v) { this.isAvailableToRent = v; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder areaM2(BigDecimal areaM2) { this.areaM2 = areaM2; return this; }
        public Builder bedrooms(Integer bedrooms) { this.bedrooms = bedrooms; return this; }
        public Builder bathrooms(BigDecimal bathrooms) { this.bathrooms = bathrooms; return this; }
        public Builder parkingSpaces(Integer parkingSpaces) { this.parkingSpaces = parkingSpaces; return this; }
        public Builder constructionYear(Integer constructionYear) { this.constructionYear = constructionYear; return this; }
        public Builder isCondominium(boolean v) { this.isCondominium = v; return this; }
        public Builder listedPrice(BigDecimal listedPrice) { this.listedPrice = listedPrice; return this; }
        public Builder pricePerM2(BigDecimal pricePerM2) { this.pricePerM2 = pricePerM2; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder propertyType(PropertyDetailPropertyTypeDto propertyType) { this.propertyType = propertyType; return this; }
        public Builder address(PropertyDetailAddressDto address) { this.address = address; return this; }
        public Builder amenities(List<AmenityResponseDto> amenities) { this.amenities = amenities; return this; }
        public Builder like(boolean like) { this.like = like; return this; }
        public Builder lessor(PropertyDetailLessorDto lessor) { this.lessor = lessor; return this; }

        public PropertyDetailContentDto build() { return new PropertyDetailContentDto(this); }
    }
}
