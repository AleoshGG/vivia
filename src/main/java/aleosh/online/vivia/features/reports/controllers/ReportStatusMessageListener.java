package aleosh.online.vivia.features.reports.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class ReportStatusMessageListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(ReportStatusMessageListener.class);

    private final AdminReportSseRegistry adminReportSseRegistry;
    private final ObjectMapper objectMapper;

    public ReportStatusMessageListener(AdminReportSseRegistry adminReportSseRegistry, ObjectMapper objectMapper) {
        this.adminReportSseRegistry = adminReportSseRegistry;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);

        String eventName = "report_new";
        String data = payload;

        try {
            JsonNode node = objectMapper.readTree(payload);
            if (node.has("event")) {
                eventName = node.get("event").asText();
                data = node.get("data").toString();
            }
        } catch (Exception e) {
            log.debug("Payload no tipado en canal de reportes, se usa como está");
        }

        adminReportSseRegistry.broadcast(eventName, data);
    }
}
