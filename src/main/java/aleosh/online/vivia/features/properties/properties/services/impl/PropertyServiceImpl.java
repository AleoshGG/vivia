package aleosh.online.vivia.features.properties.properties.services.impl;

import aleosh.online.vivia.features.address.address.domain.entities.Address;
import aleosh.online.vivia.features.address.address.domain.repositories.IAddressRepository;
import aleosh.online.vivia.features.properties.likes.domain.repositories.IPropertyLikeRepository;
import aleosh.online.vivia.features.properties.properties.data.dtos.request.CreatePropertyDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.request.PropertyMediaDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyDetailResponseDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyPreviewResponseDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyResponseDto;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyEntity;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyMediaEntity;
import aleosh.online.vivia.features.properties.properties.data.repositories.PropertyRepository;
import aleosh.online.vivia.features.properties.properties.domain.entities.Property;
import aleosh.online.vivia.features.properties.properties.domain.exceptions.PropertyNotFoundException;
import aleosh.online.vivia.features.properties.properties.domain.repositories.IPropertyRepository;
import aleosh.online.vivia.features.properties.properties.services.IPropertyService;
import aleosh.online.vivia.features.properties.properties.services.mappers.PropertyDetailMapper;
import aleosh.online.vivia.features.properties.properties.services.mappers.PropertyMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public PropertyServiceImpl(
            IPropertyRepository propertyRepository,
            IAddressRepository addressRepository,
            PropertyRepository propertyJpaRepository,
            @Qualifier("propertyServiceMapper") PropertyMapper mapper,
            PropertyDetailMapper detailMapper,
            IPropertyLikeRepository likeRepository
    ) {
        this.propertyRepository = propertyRepository;
        this.addressRepository = addressRepository;
        this.propertyJpaRepository = propertyJpaRepository;
        this.mapper = mapper;
        this.detailMapper = detailMapper;
        this.likeRepository = likeRepository;
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
        PropertyEntity entity = propertyJpaRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with id: " + propertyId));

        boolean liked = isLessee && likeRepository.existsByUserIdAndPropertyId(userId, propertyId);

        return detailMapper.toDetailResponse(entity, isLessee, liked);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyResponseDto> getAll() {
        return propertyJpaRepository.findAll().stream()
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
        PropertyEntity propertyEntity = propertyJpaRepository.findByLessorId(lessorId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found for lessor id: " + lessorId));

        return mapper.toResponseDtoWithMedia(propertyEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyPreviewResponseDto> getMyProperties(UUID lessorId, Integer limit) {
        Stream<PropertyPreviewResponseDto> stream = propertyJpaRepository.findAllByLessorId(lessorId)
                .stream()
                .map(mapper::toPreviewDto);
        if (limit != null) {
            stream = stream.limit(limit);
        }
        return stream.collect(Collectors.toList());
    }
}
