package aleosh.online.vivia.features.properties.media.data.repositories;

import aleosh.online.vivia.features.properties.media.domain.entities.MediaUploadSession;
import aleosh.online.vivia.features.properties.media.domain.repositories.IMediaUploadSessionRepository;
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
public class MediaUploadSessionRedisRepository implements IMediaUploadSessionRepository {

    private static final String KEY_PREFIX = "property:media-session:";
    private static final String FIELD_DATA = "data";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_UPLOADED_FILES = "uploadedFiles";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${vivia.property.media-session.ttl-hours:2}")
    private int ttlHours;

    public MediaUploadSessionRedisRepository(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public MediaUploadSession save(MediaUploadSession session) {
        String key = buildKey(session.getId());
        String json = serialize(session);

        redisTemplate.opsForHash().put(key, FIELD_DATA, json);
        redisTemplate.opsForHash().put(key, FIELD_STATUS, session.getStatus());
        redisTemplate.opsForHash().put(key, FIELD_UPLOADED_FILES, "0");

        long secondsUntilExpiry = Duration.between(Instant.now(), session.getExpiresAt()).getSeconds();
        redisTemplate.expire(key, Duration.ofSeconds(Math.max(secondsUntilExpiry, 1)));

        return session;
    }

    @Override
    public Optional<MediaUploadSession> getById(UUID id) {
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
    public void updateStatus(UUID sessionId, String newStatus) {
        String key = buildKey(sessionId);
        redisTemplate.opsForHash().put(key, FIELD_STATUS, newStatus);

        Object json = redisTemplate.opsForHash().get(key, FIELD_DATA);
        if (json == null) {
            return;
        }
        MediaUploadSession current = deserialize(json.toString());
        MediaUploadSession updated = rebuildWithStatus(current, newStatus);
        redisTemplate.opsForHash().put(key, FIELD_DATA, serialize(updated));
    }

    @Override
    public int incrementUploadedFiles(UUID sessionId) {
        Long newCount = redisTemplate.opsForHash().increment(buildKey(sessionId), FIELD_UPLOADED_FILES, 1);
        return newCount != null ? newCount.intValue() : 0;
    }

    @Override
    public int getUploadedFilesCount(UUID sessionId) {
        Object value = redisTemplate.opsForHash().get(buildKey(sessionId), FIELD_UPLOADED_FILES);
        if (value == null) {
            return 0;
        }
        return Integer.parseInt(value.toString());
    }

    private String buildKey(UUID sessionId) {
        return KEY_PREFIX + sessionId;
    }

    private String serialize(MediaUploadSession session) {
        try {
            return objectMapper.writeValueAsString(session);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error serializando MediaUploadSession a Redis", e);
        }
    }

    private MediaUploadSession deserialize(String json) {
        try {
            return objectMapper.readValue(json, MediaUploadSession.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error deserializando MediaUploadSession de Redis", e);
        }
    }

    private MediaUploadSession rebuildWithStatus(MediaUploadSession session, String newStatus) {
        return MediaUploadSession.builder()
                .id(session.getId())
                .propertyId(session.getPropertyId())
                .lessorId(session.getLessorId())
                .mediaFiles(session.getMediaFiles())
                .status(newStatus)
                .totalFiles(session.getTotalFiles())
                .createdAt(session.getCreatedAt())
                .expiresAt(session.getExpiresAt())
                .build();
    }
}
