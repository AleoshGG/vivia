package aleosh.online.vivia.features.properties.draft.controllers;

import aleosh.online.vivia.core.config.messaging.RedisPubSubConfig;
import aleosh.online.vivia.features.properties.draft.data.dtos.response.PublicationFailedSseDto;
import aleosh.online.vivia.features.properties.draft.data.dtos.response.PublishedPropertySseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DraftSsePublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public DraftSsePublisher(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishSuccess(UUID draftId, PublishedPropertySseDto dto) {
        publish(draftId, "publication_success", dto);
    }

    public void publishFailure(UUID draftId, PublicationFailedSseDto dto) {
        publish(draftId, "publication_failed", dto);
    }

    private void publish(UUID draftId, String eventName, Object payload) {
        try {
            String data = objectMapper.writeValueAsString(payload);
            String envelope = String.format("{\"event\":\"%s\",\"data\":%s}", eventName, data);
            redisTemplate.convertAndSend(RedisPubSubConfig.DRAFT_STATUS_CHANNEL_PREFIX + draftId, envelope);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error serializando evento SSE para draftId=" + draftId, e);
        }
    }
}
