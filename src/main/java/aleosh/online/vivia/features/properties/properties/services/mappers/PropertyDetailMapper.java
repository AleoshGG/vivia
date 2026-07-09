package aleosh.online.vivia.features.properties.properties.services.mappers;

import aleosh.online.vivia.features.properties.amenity.data.dtos.response.AmenityResponseDto;
import aleosh.online.vivia.features.properties.amenity.data.entities.AmenityEntity;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyDetailAddressDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyDetailContentDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyDetailLessorDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyDetailNeighborhoodDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyDetailPropertyTypeDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyDetailResponseDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyMediaResponseDto;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyEntity;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyMediaEntity;
import aleosh.online.vivia.features.address.address.data.entities.AddressEntity;
import aleosh.online.vivia.features.address.neighborhoods.data.entities.NeighborhoodEntity;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PropertyDetailMapper {

    public PropertyDetailResponseDto toDetailResponse(PropertyEntity entity, boolean isLessee, boolean liked) {
        PropertyDetailContentDto content = buildContent(entity, isLessee, liked);
        List<PropertyMediaResponseDto> contentMedia = buildContentMedia(entity);
        return PropertyDetailResponseDto.builder()
                .content(content)
                .contentMedia(contentMedia)
                .build();
    }

    private PropertyDetailContentDto buildContent(PropertyEntity entity, boolean isLessee, boolean liked) {
        return PropertyDetailContentDto.builder()
                .id(entity.getId())
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
                .propertyType(toPropertyTypeDto(entity))
                .address(toAddressDto(entity.getAddress()))
                .amenities(toAmenityDtos(entity.getAmenities()))
                .like(liked)
                .lessor(isLessee ? toLessorDto(entity.getLessor()) : null)
                .build();
    }

    private PropertyDetailPropertyTypeDto toPropertyTypeDto(PropertyEntity entity) {
        if (entity.getPropertyType() == null) {
            return null;
        }
        return PropertyDetailPropertyTypeDto.builder()
                .id(entity.getPropertyType().getId())
                .name(entity.getPropertyType().getName())
                .build();
    }

    private PropertyDetailAddressDto toAddressDto(AddressEntity address) {
        if (address == null) {
            return null;
        }
        BigDecimal latitude = null;
        BigDecimal longitude = null;
        if (address.getLocation() != null) {
            latitude = BigDecimal.valueOf(address.getLocation().getY());
            longitude = BigDecimal.valueOf(address.getLocation().getX());
        }
        return PropertyDetailAddressDto.builder()
                .id(address.getId())
                .street(address.getStreet())
                .exteriorNumber(address.getExteriorNumber())
                .interiorNumber(address.getInteriorNumber())
                .neighborhood(toNeighborhoodDto(address.getNeighborhood()))
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    private PropertyDetailNeighborhoodDto toNeighborhoodDto(NeighborhoodEntity neighborhood) {
        if (neighborhood == null) {
            return null;
        }
        return PropertyDetailNeighborhoodDto.builder()
                .id(neighborhood.getId())
                .name(neighborhood.getName())
                .postalCode(neighborhood.getPostalCode())
                .build();
    }

    private List<AmenityResponseDto> toAmenityDtos(List<AmenityEntity> amenities) {
        if (amenities == null) {
            return new ArrayList<>();
        }
        return amenities.stream()
                .map(a -> AmenityResponseDto.builder().id(a.getId()).name(a.getName()).build())
                .collect(Collectors.toList());
    }

    private PropertyDetailLessorDto toLessorDto(LessorEntity lessor) {
        if (lessor == null) {
            return null;
        }
        UserEntity user = lessor.getUser();
        if (user == null) {
            return null;
        }
        return PropertyDetailLessorDto.builder()
                .id(lessor.getId())
                .name(user.getName())
                .paternalSurname(user.getPaternalSurname())
                .maternalSurname(user.getMaternalSurname())
                .photoUrl(user.getPhotoUrl())
                .build();
    }

    private List<PropertyMediaResponseDto> buildContentMedia(PropertyEntity entity) {
        if (entity.getMedia() == null) {
            return new ArrayList<>();
        }

        List<PropertyMediaEntity> images = entity.getMedia().stream()
                .filter(m -> PropertyMediaEntity.MediaType.IMAGE.equals(m.getType()))
                .collect(Collectors.toList());

        List<PropertyMediaResponseDto> result = new ArrayList<>();

        images.stream()
                .filter(m -> "MAIN".equals(m.getClassification()))
                .findFirst()
                .map(this::toMediaDto)
                .ifPresent(result::add);

        images.stream()
                .filter(m -> !"MAIN".equals(m.getClassification()))
                .limit(3 - result.size())
                .map(this::toMediaDto)
                .forEach(result::add);

        return result;
    }

    private PropertyMediaResponseDto toMediaDto(PropertyMediaEntity media) {
        return PropertyMediaResponseDto.builder()
                .id(media.getId())
                .url(media.getUrl())
                .type(media.getType() != null ? media.getType().name() : null)
                .classification(media.getClassification())
                .build();
    }
}
