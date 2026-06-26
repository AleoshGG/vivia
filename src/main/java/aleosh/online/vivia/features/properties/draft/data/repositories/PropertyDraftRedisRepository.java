package aleosh.online.vivia.features.properties.draft.data.repositories;

import aleosh.online.vivia.core.config.messaging.RedisPubSubConfig;
import aleosh.online.vivia.features.properties.draft.domain.entities.PropertyDraft;
import aleosh.online.vivia.features.properties.draft.domain.repositories.IPropertyDraftRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PropertyDraftRedisRepository implements IPropertyDraftRepository {

    private static final String KEY_PREFIX = "property:draft:";
    private static final String FIELD_DATA = "data";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_UPLOADED_FILES = "uploadedFiles";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${vivia.property.draft.ttl-hours}")
    private int ttlHours;

    public PropertyDraftRedisRepository(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public PropertyDraft save(PropertyDraft draft) {
        String key = buildKey(draft.getId());
        String json = serialize(draft);

        redisTemplate.opsForHash().put(key, FIELD_DATA, json);
        redisTemplate.opsForHash().put(key, FIELD_STATUS, draft.getStatus());
        redisTemplate.opsForHash().put(key, FIELD_UPLOADED_FILES, "0");

        long secondsUntilExpiry = Duration.between(Instant.now(), draft.getExpiresAt()).getSeconds();
        redisTemplate.expire(key, Duration.ofSeconds(secondsUntilExpiry));

        return draft;
    }

    @Override
    public Optional<PropertyDraft> getById(UUID id) {
        String key = buildKey(id);
        Object json = redisTemplate.opsForHash().get(key, FIELD_DATA);
        if (json == null) {
            return Optional.empty();
        }
        return Optional.of(deserialize(json.toString()));
    }

    @Override
    public void deleteById(UUID id) {
        redisTemplate.delete(buildKey(id));
    }

    @Override
    public void updateStatus(UUID draftId, String newStatus) {
        String key = buildKey(draftId);
        redisTemplate.opsForHash().put(key, FIELD_STATUS, newStatus);

        Object json = redisTemplate.opsForHash().get(key, FIELD_DATA);
        if (json == null) {
            return;
        }
        PropertyDraft current = deserialize(json.toString());
        PropertyDraft updated = rebuildWithStatus(current, newStatus);
        redisTemplate.opsForHash().put(key, FIELD_DATA, serialize(updated));

        // Pub/Sub: notifica a los SSE conectados a este draft
        String channel = RedisPubSubConfig.DRAFT_STATUS_CHANNEL_PREFIX + draftId;
        String payload = String.format("{\"draftId\":\"%s\",\"status\":\"%s\",\"updatedAt\":\"%s\"}",
                draftId, newStatus, Instant.now());
        redisTemplate.convertAndSend(channel, payload);
    }

    @Override
    public int incrementUploadedFiles(UUID draftId) {
        Long newCount = redisTemplate.opsForHash().increment(buildKey(draftId), FIELD_UPLOADED_FILES, 1);
        return newCount != null ? newCount.intValue() : 0;
    }

    @Override
    public int getUploadedFilesCount(UUID draftId) {
        Object value = redisTemplate.opsForHash().get(buildKey(draftId), FIELD_UPLOADED_FILES);
        if (value == null) {
            return 0;
        }
        return Integer.parseInt(value.toString());
    }

    private String buildKey(UUID draftId) {
        return KEY_PREFIX + draftId;
    }

    private String serialize(PropertyDraft draft) {
        try {
            return objectMapper.writeValueAsString(draft);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error serializando PropertyDraft a Redis", e);
        }
    }

    private PropertyDraft deserialize(String json) {
        try {
            return objectMapper.readValue(json, PropertyDraft.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error deserializando PropertyDraft de Redis", e);
        }
    }

    // Reconstruye el draft con un nuevo status sin perder ningún campo
    private PropertyDraft rebuildWithStatus(PropertyDraft draft, String newStatus) {
        return PropertyDraft.builder()
                .id(draft.getId())
                .lessorId(draft.getLessorId())
                .propertyType(draft.getPropertyType())
                .address(draft.getAddress())
                .mediaFiles(draft.getMediaFiles())
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
                .status(newStatus)
                .totalFiles(draft.getTotalFiles())
                .totalImages(draft.getTotalImages())
                .totalVideos(draft.getTotalVideos())
                .approvedFiles(draft.getApprovedFiles())
                .rejectedFiles(draft.getRejectedFiles())
                .amenityIds(draft.getAmenityIds())
                .createdAt(draft.getCreatedAt())
                .updatedAt(Instant.now())
                .expiresAt(draft.getExpiresAt())
                .build();
    }
}
