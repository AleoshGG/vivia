package aleosh.online.vivia.features.properties.properties.data.mappers;

import aleosh.online.vivia.features.address.address.data.entities.AddressEntity;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyEntity;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyTypeEntity;
import aleosh.online.vivia.features.properties.properties.domain.entities.Property;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import org.springframework.stereotype.Component;

@Component("propertyDataMapper")
public class PropertyMapper {

    public Property toDomain(PropertyEntity entity) {
        if (entity == null) {
            return null;
        }

        return Property.builder()
                .id(entity.getId())
                .lessorId(entity.getLessor() != null ? entity.getLessor().getId() : null)
                .propertyTypeId(entity.getPropertyType() != null ? entity.getPropertyType().getId() : null)
                .addressId(entity.getAddress() != null ? entity.getAddress().getId() : null)
                .isAvailableToRent(entity.isAvailableToRent())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .areaM2(entity.getAreaM2())
                .bedrooms(entity.getBedrooms())
                .bathrooms(entity.getBathrooms())
                .parkingSpaces(entity.getParkingSpaces())
                .constructionYear(entity.getConstructionYear())
                .isCondominium(entity.isCondominium())
                .listedPrice(entity.getListedPrice())
                .pricePerM2(entity.getPricePerM2())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public PropertyEntity toEntity(Property domain) {
        if (domain == null) {
            return null;
        }

        // Create proxy entities with only IDs
        LessorEntity lessor = new LessorEntity();
        lessor.setId(domain.getLessorId());

        PropertyTypeEntity propertyType = new PropertyTypeEntity();
        propertyType.setId(domain.getPropertyTypeId());

        AddressEntity address = new AddressEntity();
        address.setId(domain.getAddressId());

        return PropertyEntity.builder()
                .id(domain.getId())
                .lessor(lessor)
                .propertyType(propertyType)
                .address(address)
                .isAvailableToRent(domain.isAvailableToRent())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .areaM2(domain.getAreaM2())
                .bedrooms(domain.getBedrooms())
                .bathrooms(domain.getBathrooms())
                .parkingSpaces(domain.getParkingSpaces())
                .constructionYear(domain.getConstructionYear())
                .isCondominium(domain.isCondominium())
                .listedPrice(domain.getListedPrice())
                .pricePerM2(domain.getPricePerM2())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
