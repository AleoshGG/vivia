package aleosh.online.vivia.features.properties.draft.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

// Canal Redis: draft:status:{draftId}
// Payload estándar (updateStatus):  {"draftId":"...","status":"...","updatedAt":"..."}
// Payload tipado (DraftSsePublisher): {"event":"publication_success|publication_failed","data":{...}}
@Component
public class DraftStatusMessageListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(DraftStatusMessageListener.class);

    private final SseEmitterRegistry sseEmitterRegistry;
    private final ObjectMapper objectMapper;

    public DraftStatusMessageListener(SseEmitterRegistry sseEmitterRegistry, ObjectMapper objectMapper) {
        this.sseEmitterRegistry = sseEmitterRegistry;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);

        String[] parts = channel.split(":");
        if (parts.length < 3) {
            return;
        }

        UUID draftId;
        try {
            draftId = UUID.fromString(parts[2]);
        } catch (IllegalArgumentException e) {
            log.warn("draftId inválido en canal Redis Pub/Sub: {}", channel);
            return;
        }

        String eventName = "status_update";
        String data = payload;

        try {
            JsonNode node = objectMapper.readTree(payload);
            if (node.has("event")) {
                eventName = node.get("event").asText();
                data = node.get("data").toString();
            }
        } catch (Exception e) {
            log.debug("Payload no tipado en canal {}, se trata como status_update", channel);
        }

        sseEmitterRegistry.notifyStatusChange(draftId, eventName, data);
    }
}
