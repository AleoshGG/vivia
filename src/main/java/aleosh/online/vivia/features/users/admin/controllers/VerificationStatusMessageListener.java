package aleosh.online.vivia.features.users.admin.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class VerificationStatusMessageListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(VerificationStatusMessageListener.class);

    private final VerificationAdminSseRegistry verificationAdminSseRegistry;
    private final ObjectMapper objectMapper;

    public VerificationStatusMessageListener(
            VerificationAdminSseRegistry verificationAdminSseRegistry,
            ObjectMapper objectMapper
    ) {
        this.verificationAdminSseRegistry = verificationAdminSseRegistry;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);

        String eventName = "verification_pending";
        String data = payload;

        try {
            JsonNode node = objectMapper.readTree(payload);
            if (node.has("event")) {
                eventName = node.get("event").asText();
                data = node.get("data").toString();
            }
        } catch (Exception e) {
            log.debug("Payload no tipado en canal de verificación, se usa como está");
        }

        verificationAdminSseRegistry.broadcast(eventName, data);
    }
}
