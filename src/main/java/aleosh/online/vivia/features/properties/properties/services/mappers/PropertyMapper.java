package aleosh.online.vivia.features.properties.properties.services.mappers;

import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyMediaResponseDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyResponseDto;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyEntity;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyMediaEntity;
import aleosh.online.vivia.features.properties.properties.domain.entities.Property;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component("propertyServiceMapper")
public class PropertyMapper {

    public PropertyResponseDto toResponseDto(Property property) {
        if (property == null) {
            return null;
        }

        return PropertyResponseDto.builder()
                .id(property.getId())
                .lessorId(property.getLessorId())
                .propertyTypeId(property.getPropertyTypeId())
                .addressId(property.getAddressId())
                .isAvailableToRent(property.isAvailableToRent())
                .areaM2(property.getAreaM2())
                .bedrooms(property.getBedrooms())
                .bathrooms(property.getBathrooms())
                .parkingSpaces(property.getParkingSpaces())
                .constructionYear(property.getConstructionYear())
                .isCondominium(property.isCondominium())
                .listedPrice(property.getListedPrice())
                .pricePerM2(property.getPricePerM2())
                .createdAt(property.getCreatedAt())
                .updatedAt(property.getUpdatedAt())
                .build();
    }

    public PropertyResponseDto toResponseDtoWithMedia(PropertyEntity entity) {
        if (entity == null) {
            return null;
        }

        List<PropertyMediaResponseDto> mediaDtos = new ArrayList<>();
        if (entity.getMedia() != null) {
            mediaDtos = entity.getMedia().stream()
                    .map(this::toMediaResponseDto)
                    .collect(Collectors.toList());
        }

        return PropertyResponseDto.builder()
                .id(entity.getId())
                .lessorId(entity.getLessor() != null ? entity.getLessor().getId() : null)
                .propertyTypeId(entity.getPropertyType() != null ? entity.getPropertyType().getId() : null)
                .addressId(entity.getAddress() != null ? entity.getAddress().getId() : null)
                .isAvailableToRent(entity.isAvailableToRent())
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
                .media(mediaDtos)
                .build();
    }

    private PropertyMediaResponseDto toMediaResponseDto(PropertyMediaEntity entity) {
        if (entity == null) {
            return null;
        }

        return PropertyMediaResponseDto.builder()
                .id(entity.getId())
                .url(entity.getUrl())
                .type(entity.getType() != null ? entity.getType().name() : null)
                .classification(entity.getClassification())
                .build();
    }
}
