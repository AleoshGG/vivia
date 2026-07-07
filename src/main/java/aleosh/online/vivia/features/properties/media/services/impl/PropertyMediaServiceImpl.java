package aleosh.online.vivia.features.properties.media.services.impl;

import aleosh.online.vivia.features.properties.media.data.dtos.request.AddPropertyMediaDto;
import aleosh.online.vivia.features.properties.media.data.dtos.request.ChangeMainImageDto;
import aleosh.online.vivia.features.properties.media.data.dtos.request.PropertyMediaManifestItemDto;
import aleosh.online.vivia.features.properties.media.data.dtos.response.MediaUploadSessionResponseDto;
import aleosh.online.vivia.features.properties.media.data.dtos.response.PropertyMediaUploadUrlDto;
import aleosh.online.vivia.features.properties.media.domain.entities.MediaUploadSession;
import aleosh.online.vivia.features.properties.media.domain.entities.MediaUploadSessionItem;
import aleosh.online.vivia.features.properties.media.domain.exceptions.InvalidMediaOperationException;
import aleosh.online.vivia.features.properties.media.domain.exceptions.MainImageDeletionException;
import aleosh.online.vivia.features.properties.media.domain.exceptions.MediaNotFoundException;
import aleosh.online.vivia.features.properties.media.domain.exceptions.MediaOwnershipException;
import aleosh.online.vivia.features.properties.media.domain.repositories.IMediaUploadSessionRepository;
import aleosh.online.vivia.features.properties.media.services.IPropertyMediaService;
import aleosh.online.vivia.features.properties.media.services.IPropertyMediaStorageService;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyEntity;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyMediaEntity;
import aleosh.online.vivia.features.properties.properties.data.repositories.PropertyMediaRepository;
import aleosh.online.vivia.features.properties.properties.data.repositories.PropertyRepository;
import aleosh.online.vivia.features.properties.properties.domain.exceptions.PropertyNotFoundException;
import aleosh.online.vivia.features.properties.properties.domain.exceptions.PropertyOwnershipException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PropertyMediaServiceImpl implements IPropertyMediaService {

    private final PropertyMediaRepository propertyMediaRepository;
    private final PropertyRepository propertyRepository;
    private final IMediaUploadSessionRepository sessionRepository;
    private final IPropertyMediaStorageService storageService;

    @Value("${vivia.property.media-session.ttl-hours:2}")
    private int sessionTtlHours;

    public PropertyMediaServiceImpl(
            PropertyMediaRepository propertyMediaRepository,
            PropertyRepository propertyRepository,
            IMediaUploadSessionRepository sessionRepository,
            IPropertyMediaStorageService storageService
    ) {
        this.propertyMediaRepository = propertyMediaRepository;
        this.propertyRepository = propertyRepository;
        this.sessionRepository = sessionRepository;
        this.storageService = storageService;
    }

    @Override
    @Transactional
    public void deleteMedia(UUID mediaId, UUID lessorId) {
        PropertyMediaEntity media = propertyMediaRepository.findById(mediaId)
                .orElseThrow(() -> new MediaNotFoundException("Media not found with id: " + mediaId));

        if (!media.getProperty().getLessor().getId().equals(lessorId)) {
            throw new MediaOwnershipException("Media does not belong to the authenticated lessor");
        }

        if (media.getType() == PropertyMediaEntity.MediaType.IMAGE
                && "MAIN".equalsIgnoreCase(media.getClassification())) {
            throw new MainImageDeletionException("You cannot delete the main image of the property.");
        }

        String s3Key = extractS3Key(media.getUrl());
        storageService.deleteObject(s3Key);

        propertyMediaRepository.deleteById(mediaId);
    }

    @Override
    @Transactional
    public void changeMainImage(ChangeMainImageDto dto, UUID lessorId) {
        if (dto.getMainImageId().equals(dto.getNewMainImageId())) {
            throw new InvalidMediaOperationException("main_image_id and new_main_image_id must be different");
        }

        PropertyMediaEntity current = propertyMediaRepository.findById(dto.getMainImageId())
                .orElseThrow(() -> new MediaNotFoundException("Media not found with id: " + dto.getMainImageId()));

        PropertyMediaEntity next = propertyMediaRepository.findById(dto.getNewMainImageId())
                .orElseThrow(() -> new MediaNotFoundException("Media not found with id: " + dto.getNewMainImageId()));

        if (!current.getProperty().getLessor().getId().equals(lessorId)
                || !next.getProperty().getLessor().getId().equals(lessorId)) {
            throw new MediaOwnershipException("Media does not belong to the authenticated lessor");
        }

        if (!current.getProperty().getId().equals(next.getProperty().getId())) {
            throw new InvalidMediaOperationException("Both images must belong to the same property");
        }

        if (!"MAIN".equalsIgnoreCase(current.getClassification())) {
            throw new InvalidMediaOperationException("main_image_id does not have classification MAIN");
        }

        if (next.getType() != PropertyMediaEntity.MediaType.IMAGE) {
            throw new InvalidMediaOperationException("New main image must be an IMAGE");
        }

        current.setClassification("OTHER");
        next.setClassification("MAIN");

        propertyMediaRepository.save(current);
        propertyMediaRepository.save(next);
    }

    @Override
    public MediaUploadSessionResponseDto addMedia(AddPropertyMediaDto dto, UUID lessorId) {
        PropertyEntity property = propertyRepository.findByIdAndDeletedAtIsNull(dto.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with id: " + dto.getPropertyId()));

        if (!property.getLessor().getId().equals(lessorId)) {
            throw new PropertyOwnershipException("Property does not belong to the authenticated lessor");
        }

        List<PropertyMediaManifestItemDto> manifest = dto.getMediaManifest();

        boolean hasMain = manifest.stream()
                .anyMatch(item -> "MAIN".equalsIgnoreCase(item.getClassification()));
        if (hasMain) {
            throw new InvalidMediaOperationException("Media manifest cannot contain MAIN classification");
        }

        Set<String> fileKeys = new HashSet<>();
        for (PropertyMediaManifestItemDto item : manifest) {
            if (!fileKeys.add(item.getFileKey())) {
                throw new InvalidMediaOperationException("Duplicate fileKey in manifest: " + item.getFileKey());
            }
        }

        UUID sessionId = UUID.randomUUID();
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds((long) sessionTtlHours * 3600);

        List<PropertyMediaUploadUrlDto> uploadUrls = storageService.generateUploadUrls(sessionId, manifest);

        Map<String, MediaUploadSessionItem> mediaFiles = new LinkedHashMap<>();
        for (int i = 0; i < manifest.size(); i++) {
            PropertyMediaManifestItemDto item = manifest.get(i);
            mediaFiles.put(item.getFileKey(), new MediaUploadSessionItem(
                    item.getFileKey(),
                    item.getContentType(),
                    item.getSizeBytes(),
                    item.getClassification(),
                    uploadUrls.get(i).getStorageKey()
            ));
        }

        MediaUploadSession session = MediaUploadSession.builder()
                .id(sessionId)
                .propertyId(dto.getPropertyId())
                .lessorId(lessorId)
                .mediaFiles(mediaFiles)
                .status("MEDIA_UPLOAD_PENDING")
                .totalFiles(manifest.size())
                .createdAt(now)
                .expiresAt(expiresAt)
                .build();

        sessionRepository.save(session);

        return MediaUploadSessionResponseDto.builder()
                .sessionId(sessionId)
                .propertyId(dto.getPropertyId())
                .status("MEDIA_UPLOAD_PENDING")
                .expiresAt(expiresAt)
                .uploads(uploadUrls)
                .build();
    }

    private String extractS3Key(String url) {
        try {
            URI uri = URI.create(url);
            String path = uri.getPath();
            // path starts with "/" so remove the leading slash
            return path.startsWith("/") ? path.substring(1) : path;
        } catch (Exception e) {
            // fallback: strip the scheme+host prefix manually
            int idx = url.indexOf(".amazonaws.com/");
            if (idx >= 0) {
                return url.substring(idx + ".amazonaws.com/".length());
            }
            return url;
        }
    }
}
