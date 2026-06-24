package aleosh.online.vivia.features.properties.draft.services.impl;

import aleosh.online.vivia.features.address.neighborhoods.data.entities.NeighborhoodEntity;
import aleosh.online.vivia.features.address.neighborhoods.data.repositories.NeighborhoodRepository;
import aleosh.online.vivia.features.properties.draft.data.dtos.request.CreatePropertyDraftRequestDto;
import aleosh.online.vivia.features.properties.draft.data.dtos.request.MediaManifestItemDto;
import aleosh.online.vivia.features.properties.draft.data.dtos.response.CloudinaryUploadParamsDto;
import aleosh.online.vivia.features.properties.draft.data.dtos.response.CreatePropertyDraftResponseDto;
import aleosh.online.vivia.features.properties.draft.domain.entities.PropertyDraft;
import aleosh.online.vivia.features.properties.draft.domain.entities.PropertyDraftMedia;
import aleosh.online.vivia.features.properties.draft.domain.exceptions.InvalidMediaManifestException;
import aleosh.online.vivia.features.properties.draft.domain.exceptions.NeighborhoodNotFoundException;
import aleosh.online.vivia.features.properties.draft.domain.exceptions.PropertyTypeNotFoundException;
import aleosh.online.vivia.features.properties.draft.domain.repositories.IPropertyDraftRepository;
import aleosh.online.vivia.features.properties.draft.services.ICloudinaryUploadService;
import aleosh.online.vivia.features.properties.draft.services.IPropertyDraftService;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyTypeEntity;
import aleosh.online.vivia.features.properties.properties.data.repositories.PropertyTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PropertyDraftServiceImpl implements IPropertyDraftService {

    private static final Logger log = LoggerFactory.getLogger(PropertyDraftServiceImpl.class);

    private final IPropertyDraftRepository draftRepository;
    private final ICloudinaryUploadService cloudinaryUploadService;
    private final PropertyTypeRepository propertyTypeRepository;
    private final NeighborhoodRepository neighborhoodRepository;

    @Value("${vivia.property.draft.ttl-hours}")
    private int draftTtlHours;

    @Value("${vivia.property.media.min-images}")
    private int minImages;

    @Value("${vivia.property.media.max-images}")
    private int maxImages;

    @Value("${vivia.property.media.max-videos}")
    private int maxVideos;

    @Value("${vivia.property.media.max-image-size-bytes}")
    private long maxImageSizeBytes;

    @Value("${vivia.property.media.max-video-size-bytes}")
    private long maxVideoSizeBytes;

    @Value("${vivia.property.media.allowed-image-types}")
    private String allowedImageTypes;

    @Value("${vivia.property.media.allowed-video-types}")
    private String allowedVideoTypes;

    public PropertyDraftServiceImpl(
            IPropertyDraftRepository draftRepository,
            ICloudinaryUploadService cloudinaryUploadService,
            PropertyTypeRepository propertyTypeRepository,
            NeighborhoodRepository neighborhoodRepository
    ) {
        this.draftRepository = draftRepository;
        this.cloudinaryUploadService = cloudinaryUploadService;
        this.propertyTypeRepository = propertyTypeRepository;
        this.neighborhoodRepository = neighborhoodRepository;
    }

    @Override
    public CreatePropertyDraftResponseDto createDraft(CreatePropertyDraftRequestDto request, UUID lessorId) {
        log.info("[PIPELINE] [1/6] Iniciando createDraft: lessorId={}, archivos={}, propertyTypeId={}, neighborhoodId={}",
                lessorId, request.getMediaManifest().size(), request.getPropertyTypeId(), request.getNeighborhoodId());

        validateMediaManifest(request.getMediaManifest());
        log.info("[PIPELINE] [2/6] Manifest validado: {} imágenes y {} videos",
                request.getMediaManifest().stream().filter(i -> i.getContentType().startsWith("image/")).count(),
                request.getMediaManifest().stream().filter(i -> i.getContentType().startsWith("video/")).count());

        PropertyTypeEntity propertyType = propertyTypeRepository.findById(request.getPropertyTypeId())
                .orElseThrow(() -> new PropertyTypeNotFoundException(
                        "Property type not found with ID: " + request.getPropertyTypeId()
                ));
        log.info("[PIPELINE] [3/6] PropertyType encontrado: id={}, name={}", propertyType.getId(), propertyType.getName());

        NeighborhoodEntity neighborhood = neighborhoodRepository.findById(request.getNeighborhoodId())
                .orElseThrow(() -> new NeighborhoodNotFoundException(
                        "Neighborhood not found with ID: " + request.getNeighborhoodId()
                ));
        log.info("[PIPELINE] [3/6] Neighborhood encontrado: id={}, name={}", neighborhood.getId(), neighborhood.getName());

        BigDecimal pricePerM2 = request.getListedPrice()
                .divide(request.getAreaM2(), 2, RoundingMode.HALF_UP);

        UUID draftId = UUID.randomUUID();
        log.info("[PIPELINE] [4/6] draftId generado: {}", draftId);

        List<CloudinaryUploadParamsDto> uploadParams = cloudinaryUploadService
                .generateSignedUploadParams(draftId, request.getMediaManifest());
        log.info("[PIPELINE] [4/6] Signed upload params generados para {} archivos. Primer uploadUrl: {}",
                uploadParams.size(), uploadParams.isEmpty() ? "N/A" : uploadParams.get(0).getUploadUrl());

        Instant now = Instant.now();
        Instant expiresAt = now.plus(Duration.ofHours(draftTtlHours));

        List<MediaManifestItemDto> images = request.getMediaManifest().stream()
                .filter(item -> item.getContentType().startsWith("image/"))
                .collect(Collectors.toList());

        List<MediaManifestItemDto> videos = request.getMediaManifest().stream()
                .filter(item -> item.getContentType().startsWith("video/"))
                .collect(Collectors.toList());

        Map<String, PropertyDraftMedia> mediaFiles = new HashMap<>();
        for (MediaManifestItemDto item : request.getMediaManifest()) {
            String publicId = cloudinaryUploadService.buildPublicId(draftId, item.getFileKey());
            PropertyDraftMedia media = new PropertyDraftMedia(
                    UUID.randomUUID(),
                    draftId,
                    item.getFileKey(),
                    item.getContentType(),
                    publicId,
                    "PENDING",
                    item.getClassification()
            );
            mediaFiles.put(item.getFileKey(), media);
            log.debug("[PIPELINE] Media registrada: fileKey={}, publicId={}, contentType={}",
                    item.getFileKey(), publicId, item.getContentType());
        }

        PropertyDraft.PropertyTypeData propertyTypeData = new PropertyDraft.PropertyTypeData(
                propertyType.getId(),
                propertyType.getName()
        );

        PropertyDraft.AddressData addressData = new PropertyDraft.AddressData(
                UUID.randomUUID(),
                neighborhood.getId(),
                neighborhood.getName(),
                neighborhood.getPostalCode(),
                request.getStreet(),
                request.getExteriorNumber(),
                request.getInteriorNumber()
        );

        PropertyDraft draft = PropertyDraft.builder()
                .id(draftId)
                .lessorId(lessorId)
                .propertyType(propertyTypeData)
                .address(addressData)
                .mediaFiles(mediaFiles)
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
                .pricePerM2(pricePerM2)
                .status("PENDING_MEDIA")
                .totalFiles(request.getMediaManifest().size())
                .totalImages(images.size())
                .totalVideos(videos.size())
                .approvedFiles(0)
                .rejectedFiles(0)
                .createdAt(now)
                .updatedAt(now)
                .expiresAt(expiresAt)
                .build();

        log.info("[PIPELINE] [5/6] Draft construido: draftId={}, status=PENDING_MEDIA, totalFiles={}, expiresAt={}",
                draftId, draft.getTotalFiles(), expiresAt);

        draftRepository.save(draft);
        log.info("[PIPELINE] [6/6] Draft guardado en Redis. draftId={} listo para recibir uploads de Cloudinary.", draftId);

        return new CreatePropertyDraftResponseDto(
                draftId,
                "PENDING_MEDIA",
                expiresAt.toString(),
                uploadParams
        );
    }

    private void validateMediaManifest(List<MediaManifestItemDto> manifest) {
        Set<String> fileKeys = new HashSet<>();
        for (MediaManifestItemDto item : manifest) {
            if (!fileKeys.add(item.getFileKey())) {
                throw new InvalidMediaManifestException("Duplicate file key found: " + item.getFileKey());
            }
        }

        Set<String> allowedImageTypesSet = Set.of(allowedImageTypes.split(","));
        Set<String> allowedVideoTypesSet = Set.of(allowedVideoTypes.split(","));

        List<MediaManifestItemDto> images = new ArrayList<>();
        List<MediaManifestItemDto> videos = new ArrayList<>();

        for (MediaManifestItemDto item : manifest) {
            String contentType = item.getContentType().toLowerCase();
            if (allowedImageTypesSet.contains(contentType)) {
                images.add(item);
            } else if (allowedVideoTypesSet.contains(contentType)) {
                videos.add(item);
            } else {
                throw new InvalidMediaManifestException("Content type not allowed: " + contentType);
            }
        }

        if (images.size() < minImages) {
            throw new InvalidMediaManifestException(
                    String.format("At least %d images are required, but got %d", minImages, images.size()));
        }
        if (images.size() > maxImages) {
            throw new InvalidMediaManifestException(
                    String.format("Maximum %d images allowed, but got %d", maxImages, images.size()));
        }
        if (videos.size() > maxVideos) {
            throw new InvalidMediaManifestException(
                    String.format("Maximum %d videos allowed, but got %d", maxVideos, videos.size()));
        }

        for (MediaManifestItemDto image : images) {
            if (image.getSizeBytes() > maxImageSizeBytes) {
                throw new InvalidMediaManifestException(
                        String.format("Image %s exceeds maximum size of %d bytes",
                                image.getFileKey(), maxImageSizeBytes));
            }
        }
        for (MediaManifestItemDto video : videos) {
            if (video.getSizeBytes() > maxVideoSizeBytes) {
                throw new InvalidMediaManifestException(
                        String.format("Video %s exceeds maximum size of %d bytes",
                                video.getFileKey(), maxVideoSizeBytes));
            }
        }
    }
}
