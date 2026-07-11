package aleosh.online.vivia.features.properties.properties.services.impl;

import aleosh.online.vivia.features.address.address.data.entities.AddressEntity;
import aleosh.online.vivia.features.address.address.data.mappers.AddressMapper;
import aleosh.online.vivia.features.address.address.domain.entities.Address;
import aleosh.online.vivia.features.address.address.domain.exceptions.InvalidAddressException;
import aleosh.online.vivia.features.address.address.domain.repositories.IAddressRepository;
import aleosh.online.vivia.features.address.neighborhoods.data.entities.NeighborhoodEntity;
import aleosh.online.vivia.features.address.neighborhoods.data.repositories.NeighborhoodRepository;
import aleosh.online.vivia.features.address.neighborhoods.domain.exceptions.NeighborhoodNotFoundException;
import aleosh.online.vivia.features.properties.amenity.data.entities.AmenityEntity;
import aleosh.online.vivia.features.properties.amenity.data.repositories.AmenityRepository;
import aleosh.online.vivia.features.properties.amenity.domain.exceptions.AmenityNotFoundException;
import aleosh.online.vivia.features.properties.likes.domain.repositories.IPropertyLikeRepository;
import aleosh.online.vivia.features.properties.properties.data.dtos.request.CreatePropertyDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.request.PropertyMediaDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.request.UpdateAddressPropertyDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.request.UpdatePropertyDto;
import aleosh.online.vivia.features.properties.properties.domain.exceptions.PropertyOwnershipException;
import aleosh.online.vivia.features.properties.properties.domain.exceptions.PropertyTypeNotFoundException;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyDetailResponseDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyMediaResponseDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyPreviewResponseDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyResponseDto;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyEntity;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyMediaEntity;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyTypeEntity;
import aleosh.online.vivia.features.properties.properties.data.repositories.PropertyMediaRepository;
import aleosh.online.vivia.features.properties.properties.data.repositories.PropertyRepository;
import aleosh.online.vivia.features.properties.properties.data.repositories.PropertyTypeRepository;
import aleosh.online.vivia.features.properties.properties.domain.entities.Property;
import aleosh.online.vivia.features.properties.properties.domain.exceptions.PropertyNotFoundException;
import aleosh.online.vivia.features.properties.properties.domain.repositories.IPropertyRepository;
import aleosh.online.vivia.features.properties.properties.services.IPropertyService;
import aleosh.online.vivia.features.properties.properties.services.mappers.PropertyDetailMapper;
import aleosh.online.vivia.features.properties.properties.services.mappers.PropertyMapper;
import aleosh.online.vivia.features.users.lessee.services.ILesseeService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PropertyServiceImpl implements IPropertyService {

    private final IPropertyRepository propertyRepository;
    private final IAddressRepository addressRepository;
    private final PropertyRepository propertyJpaRepository;
    private final PropertyMapper mapper;
    private final PropertyDetailMapper detailMapper;
    private final IPropertyLikeRepository likeRepository;
    private final PropertyMediaRepository propertyMediaRepository;
    private final ILesseeService lesseeService;
    private final PropertyTypeRepository propertyTypeRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final AmenityRepository amenityRepository;
    private final AddressMapper addressDataMapper;

    public PropertyServiceImpl(
            IPropertyRepository propertyRepository,
            IAddressRepository addressRepository,
            PropertyRepository propertyJpaRepository,
            @Qualifier("propertyServiceMapper") PropertyMapper mapper,
            PropertyDetailMapper detailMapper,
            IPropertyLikeRepository likeRepository,
            PropertyMediaRepository propertyMediaRepository,
            ILesseeService lesseeService,
            PropertyTypeRepository propertyTypeRepository,
            NeighborhoodRepository neighborhoodRepository,
            AmenityRepository amenityRepository,
            @Qualifier("addressDataMapper") AddressMapper addressDataMapper
    ) {
        this.propertyRepository = propertyRepository;
        this.addressRepository = addressRepository;
        this.propertyJpaRepository = propertyJpaRepository;
        this.mapper = mapper;
        this.detailMapper = detailMapper;
        this.likeRepository = likeRepository;
        this.propertyMediaRepository = propertyMediaRepository;
        this.lesseeService = lesseeService;
        this.propertyTypeRepository = propertyTypeRepository;
        this.neighborhoodRepository = neighborhoodRepository;
        this.amenityRepository = amenityRepository;
        this.addressDataMapper = addressDataMapper;
    }

    @Override
    @Transactional
    public PropertyResponseDto create(CreatePropertyDto request) {
        Address address = Address.builder()
                .id(UUID.randomUUID())
                .neighborhoodId(request.getNeighborhoodId())
                .street(request.getStreet())
                .exteriorNumber(request.getExteriorNumber())
                .interiorNumber(request.getInteriorNumber())
                .build();

        Address savedAddress = addressRepository.save(address);

        Property property = Property.builder()
                .id(UUID.randomUUID())
                .lessorId(request.getLessorId())
                .propertyTypeId(request.getPropertyTypeId())
                .addressId(savedAddress.getId())
                .isAvailableToRent(request.getIsAvailableToRent() != null ? request.getIsAvailableToRent() : false)
                .title(request.getTitle())
                .description(request.getDescription())
                .areaM2(request.getAreaM2())
                .bedrooms(request.getBedrooms())
                .bathrooms(request.getBathrooms())
                .parkingSpaces(request.getParkingSpaces())
                .constructionYear(request.getConstructionYear())
                .isCondominium(request.getIsCondominium() != null ? request.getIsCondominium() : false)
                .listedPrice(request.getListedPrice())
                .pricePerM2(request.getPricePerM2())
                .build();

        Property savedProperty = propertyRepository.save(property);

        if (request.getMedia() != null && !request.getMedia().isEmpty()) {
            PropertyEntity propertyEntity = propertyJpaRepository.findById(savedProperty.getId())
                    .orElseThrow(() -> new PropertyNotFoundException("Property not found after creation"));

            List<PropertyMediaEntity> mediaEntities = new ArrayList<>();
            for (PropertyMediaDto mediaDto : request.getMedia()) {
                PropertyMediaEntity mediaEntity = PropertyMediaEntity.builder()
                        .id(UUID.randomUUID())
                        .property(propertyEntity)
                        .url(mediaDto.getUrl())
                        .type(PropertyMediaEntity.MediaType.valueOf(mediaDto.getType().name()))
                        .classification(mediaDto.getClassification())
                        .build();
                mediaEntities.add(mediaEntity);
            }

            propertyEntity.getMedia().addAll(mediaEntities);
            propertyJpaRepository.save(propertyEntity);
        }

        PropertyEntity completeProperty = propertyJpaRepository.findById(savedProperty.getId())
                .orElseThrow(() -> new PropertyNotFoundException("Property not found after creation"));

        return mapper.toResponseDtoWithMedia(completeProperty);
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyDetailResponseDto getDetail(UUID propertyId, UUID userId, boolean isLessee) {
        PropertyEntity entity = propertyJpaRepository.findByIdAndDeletedAtIsNull(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with id: " + propertyId));

        boolean liked = isLessee && likeRepository.existsByUserIdAndPropertyId(userId, propertyId);

        return detailMapper.toDetailResponse(entity, isLessee, liked);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyResponseDto> getAll() {
        return propertyJpaRepository.findAllByDeletedAtIsNull().stream()
                .map(mapper::toResponseDtoWithMedia)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        propertyRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyResponseDto getByLessorId(UUID lessorId) {
        PropertyEntity propertyEntity = propertyJpaRepository.findByLessorIdAndDeletedAtIsNull(lessorId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found for lessor id: " + lessorId));

        return mapper.toResponseDtoWithMedia(propertyEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyMediaResponseDto> getMediaByPropertyId(UUID propertyId) {
        return propertyMediaRepository.findAllByProperty_IdAndProperty_DeletedAtIsNull(propertyId).stream()
                .map(m -> PropertyMediaResponseDto.builder()
                        .id(m.getId())
                        .url(m.getUrl())
                        .type(m.getType() != null ? m.getType().name() : null)
                        .classification(m.getClassification())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyPreviewResponseDto> getMyProperties(UUID lessorId, Integer limit) {
        Stream<PropertyPreviewResponseDto> stream = propertyJpaRepository.findAllByLessorIdAndDeletedAtIsNull(lessorId)
                .stream()
                .map(mapper::toPreviewDto);
        if (limit != null) {
            stream = stream.limit(limit);
        }
        return stream.collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyPreviewResponseDto> getSuggestions(Integer limit) {
        Stream<PropertyPreviewResponseDto> stream = propertyJpaRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc()
                .stream()
                .map(mapper::toPreviewDto);
        if (limit != null) {
            stream = stream.limit(limit);
        }
        return stream.collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PropertyResponseDto update(UUID propertyId, UpdatePropertyDto dto, UUID lessorId) {
        PropertyEntity entity = propertyJpaRepository.findByIdAndDeletedAtIsNull(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with id: " + propertyId));

        if (!entity.getLessor().getId().equals(lessorId)) {
            throw new PropertyOwnershipException("Property does not belong to the authenticated lessor");
        }

        if (dto.getTitle() != null) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getBedrooms() != null) {
            entity.setBedrooms(dto.getBedrooms());
        }
        if (dto.getParkingSpaces() != null) {
            entity.setParkingSpaces(dto.getParkingSpaces());
        }
        if (dto.getConstructionYear() != null) {
            entity.setConstructionYear(dto.getConstructionYear());
        }
        if (dto.getIsCondominium() != null) {
            entity.setCondominium(dto.getIsCondominium());
        }
        if (dto.getIsAvailableToRent() != null) {
            entity.setAvailableToRent(dto.getIsAvailableToRent());
        }

        BigDecimal area = dto.getAreaM2() != null ? dto.getAreaM2() : entity.getAreaM2();
        BigDecimal price = dto.getListedPrice() != null ? dto.getListedPrice() : entity.getListedPrice();
        BigDecimal bathrooms = dto.getBathrooms() != null ? dto.getBathrooms() : entity.getBathrooms();

        if (dto.getAreaM2() != null) {
            entity.setAreaM2(area);
        }
        if (dto.getListedPrice() != null) {
            entity.setListedPrice(price);
        }
        if (dto.getBathrooms() != null) {
            entity.setBathrooms(bathrooms);
        }

        // Recalcular pricePerM2 si cambió precio o área
        if (dto.getListedPrice() != null || dto.getAreaM2() != null) {
            if (area.compareTo(BigDecimal.ZERO) > 0) {
                entity.setPricePerM2(price.divide(area, 2, java.math.RoundingMode.HALF_UP));
            }
        }

        if (dto.getPropertyTypeId() != null) {
            PropertyTypeEntity propertyType = propertyTypeRepository.findById(dto.getPropertyTypeId())
                    .orElseThrow(() -> new PropertyTypeNotFoundException(
                            "Property type not found with id: " + dto.getPropertyTypeId()));
            entity.setPropertyType(propertyType);
        }

        if (dto.getAddress() != null) {
            updateAddress(entity.getAddress(), dto.getAddress());
        }

        if (dto.getAmenityIds() != null) {
            entity.getAmenities().clear();
            if (!dto.getAmenityIds().isEmpty()) {
                List<AmenityEntity> amenities = amenityRepository.findAllById(dto.getAmenityIds());
                if (amenities.size() != dto.getAmenityIds().stream().distinct().count()) {
                    throw new AmenityNotFoundException("One or more amenities were not found");
                }
                entity.getAmenities().addAll(amenities);
            }
        }

        PropertyEntity saved = propertyJpaRepository.save(entity);
        return mapper.toResponseDtoWithMedia(saved);
    }

    private void updateAddress(AddressEntity address, UpdateAddressPropertyDto dto) {
        if ((dto.getLatitude() == null) != (dto.getLongitude() == null)) {
            throw new InvalidAddressException("Both latitude and longitude must be provided together");
        }

        if (dto.getNeighborhoodId() != null) {
            NeighborhoodEntity neighborhood = neighborhoodRepository.findById(dto.getNeighborhoodId())
                    .orElseThrow(() -> new NeighborhoodNotFoundException(
                            "Neighborhood not found with id: " + dto.getNeighborhoodId()));
            address.setNeighborhood(neighborhood);
        }
        if (dto.getStreet() != null) {
            address.setStreet(dto.getStreet());
        }
        if (dto.getExteriorNumber() != null) {
            address.setExteriorNumber(dto.getExteriorNumber());
        }
        if (dto.getInteriorNumber() != null) {
            address.setInteriorNumber(dto.getInteriorNumber());
        }
        if (dto.getLatitude() != null) {
            address.setLocation(addressDataMapper.toPoint(dto.getLatitude(), dto.getLongitude()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyPreviewResponseDto> getNearMe(UUID lesseeId) {
        var ubication = lesseeService.getUbication(lesseeId);

        if (ubication.getLatitude() == null || ubication.getLongitude() == null) {
            return getSuggestions(5);
        }

        return propertyJpaRepository.findNearest(ubication.getLatitude(), ubication.getLongitude())
                .stream()
                .map(mapper::toPreviewDto)
                .collect(Collectors.toList());
    }
}
