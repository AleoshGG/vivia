package aleosh.online.vivia.features.properties.draft.services.impl;

import aleosh.online.vivia.features.address.address.data.entities.AddressEntity;
import aleosh.online.vivia.features.address.address.data.repositories.AddressRepository;
import aleosh.online.vivia.features.address.neighborhoods.data.entities.NeighborhoodEntity;
import aleosh.online.vivia.features.address.neighborhoods.data.repositories.NeighborhoodRepository;
import aleosh.online.vivia.features.properties.draft.domain.entities.PropertyDraft;
import aleosh.online.vivia.features.properties.draft.domain.entities.PropertyDraftMedia;
import aleosh.online.vivia.features.properties.draft.services.IPropertyPublicationService;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyEntity;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyMediaEntity;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyTypeEntity;
import aleosh.online.vivia.features.properties.properties.data.repositories.PropertyRepository;
import aleosh.online.vivia.features.properties.properties.data.repositories.PropertyTypeRepository;
import aleosh.online.vivia.features.users.lessor.data.repositories.LessorRepository;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PropertyPublicationServiceImpl implements IPropertyPublicationService {

    private final PropertyRepository propertyRepository;
    private final AddressRepository addressRepository;
    private final PropertyTypeRepository propertyTypeRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final LessorRepository lessorRepository;
    private final String bucket;
    private final String region;

    public PropertyPublicationServiceImpl(
            PropertyRepository propertyRepository,
            AddressRepository addressRepository,
            PropertyTypeRepository propertyTypeRepository,
            NeighborhoodRepository neighborhoodRepository,
            LessorRepository lessorRepository,
            @Value("${aws.s3.bucket}") String bucket,
            @Value("${aws.region}") String region
    ) {
        this.propertyRepository = propertyRepository;
        this.addressRepository = addressRepository;
        this.propertyTypeRepository = propertyTypeRepository;
        this.neighborhoodRepository = neighborhoodRepository;
        this.lessorRepository = lessorRepository;
        this.bucket = bucket;
        this.region = region;
    }

    @Override
    @Transactional
    public void publish(PropertyDraft draft) {
        LessorEntity lessor = lessorRepository.getReferenceById(draft.getLessorId());

        PropertyTypeEntity propertyType = propertyTypeRepository.getReferenceById(
                draft.getPropertyType().getId()
        );

        NeighborhoodEntity neighborhood = neighborhoodRepository.getReferenceById(
                draft.getAddress().getNeighborhoodId()
        );

        AddressEntity address = AddressEntity.builder()
                .id(UUID.randomUUID())
                .neighborhood(neighborhood)
                .street(draft.getAddress().getStreet())
                .exteriorNumber(draft.getAddress().getExteriorNumber())
                .interiorNumber(draft.getAddress().getInteriorNumber())
                .build();
        addressRepository.save(address);

        LocalDateTime now = LocalDateTime.now();

        PropertyEntity property = PropertyEntity.builder()
                .id(draft.getId())
                .lessor(lessor)
                .propertyType(propertyType)
                .address(address)
                .isAvailableToRent(draft.isAvailableToRent())
                .title(draft.getTitle())
                .description(draft.getDescription())
                .areaM2(draft.getAreaM2())
                .bedrooms(draft.getBedrooms())
                .bathrooms(draft.getBathrooms())
                .parkingSpaces(draft.getParkingSpaces())
                .constructionYear(draft.getConstructionYear())
                .isCondominium(draft.isCondominium())
                .listedPrice(draft.getListedPrice())
                .pricePerM2(draft.getPricePerM2())
                .createdAt(now)
                .updatedAt(now)
                .build();

        List<PropertyMediaEntity> mediaEntities = buildMediaEntities(draft, property);
        property.getMedia().addAll(mediaEntities);

        propertyRepository.save(property);
    }

    private List<PropertyMediaEntity> buildMediaEntities(PropertyDraft draft, PropertyEntity property) {
        List<PropertyMediaEntity> entities = new ArrayList<>();
        if (draft.getMediaFiles() == null) {
            return entities;
        }

        for (PropertyDraftMedia media : draft.getMediaFiles().values()) {
            String url = buildPublicUrl(media.getStorageKey());
            PropertyMediaEntity.MediaType type = media.getContentType().startsWith("video/")
                    ? PropertyMediaEntity.MediaType.VIDEO
                    : PropertyMediaEntity.MediaType.IMAGE;

            entities.add(PropertyMediaEntity.builder()
                    .id(UUID.randomUUID())
                    .property(property)
                    .url(url)
                    .type(type)
                    .classification(media.getClassification())
                    .build());
        }
        return entities;
    }

    // stagingKey: media/staging/<draftId>/<fileKey> → public URL: media/public/<draftId>/<fileKey>
    private String buildPublicUrl(String stagingKey) {
        String publicKey = stagingKey.replace("media/staging/", "media/public/");
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, publicKey);
    }
}
