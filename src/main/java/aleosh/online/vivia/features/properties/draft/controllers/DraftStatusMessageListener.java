package aleosh.online.vivia.features.properties.draft.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

// Canal Redis: draft:status:{draftId}
// Payload: JSON con draftId y nuevo status
@Component
public class DraftStatusMessageListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(DraftStatusMessageListener.class);

    private final SseEmitterRegistry sseEmitterRegistry;

    public DraftStatusMessageListener(SseEmitterRegistry sseEmitterRegistry) {
        this.sseEmitterRegistry = sseEmitterRegistry;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);

        // Canal: draft:status:{draftId}
        String[] parts = channel.split(":");
        if (parts.length < 3) {
            return;
        }

        try {
            UUID draftId = UUID.fromString(parts[2]);
            sseEmitterRegistry.notifyStatusChange(draftId, payload);
        } catch (IllegalArgumentException e) {
            log.warn("draftId inválido en canal Redis Pub/Sub: {}", channel);
        }
    }
}
