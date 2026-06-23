package aleosh.online.vivia.features.properties.draft.controllers;

import aleosh.online.vivia.features.properties.draft.data.dtos.request.CloudinaryWebhookPayloadDto;
import aleosh.online.vivia.features.properties.draft.messaging.events.MediaUploadedEvent;
import aleosh.online.vivia.features.properties.draft.messaging.publishers.MediaUploadEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal")
public class CloudinaryWebhookController {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryWebhookController.class);

    private final MediaUploadEventPublisher publisher;
    private final CloudinarySignatureValidator signatureValidator;
    private final ObjectMapper objectMapper;

    public CloudinaryWebhookController(
            MediaUploadEventPublisher publisher,
            CloudinarySignatureValidator signatureValidator,
            ObjectMapper objectMapper
    ) {
        this.publisher = publisher;
        this.signatureValidator = signatureValidator;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/media/uploaded")
    public ResponseEntity<Void> handleUpload(
            @RequestBody String rawBody,
            @RequestHeader(value = "X-Cld-Signature", required = false) String signature,
            @RequestHeader(value = "X-Cld-Timestamp", required = false) String timestamp
    ) {
        log.info("[PIPELINE] [WEBHOOK] Webhook recibido de Cloudinary. timestamp={}, bodyLength={}",
                timestamp, rawBody == null ? 0 : rawBody.length());

        if (!signatureValidator.isValid(rawBody, signature, timestamp)) {
            log.warn("[PIPELINE] [WEBHOOK] Firma inválida — rechazando webhook. signature={}", signature);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("[PIPELINE] [WEBHOOK] Firma válida.");

        try {
            CloudinaryWebhookPayloadDto payload = objectMapper.readValue(rawBody, CloudinaryWebhookPayloadDto.class);
            log.info("[PIPELINE] [WEBHOOK] Payload parseado: notificationType={}, publicId={}, resourceType={}",
                    payload.getNotificationType(), payload.getPublicId(), payload.getResourceType());

            if (!"upload".equals(payload.getNotificationType())) {
                log.info("[PIPELINE] [WEBHOOK] Notificación ignorada (no es upload): {}", payload.getNotificationType());
                return ResponseEntity.ok().build();
            }

            MediaUploadedEvent event = parseEvent(payload);
            if (event == null) {
                log.warn("[PIPELINE] [WEBHOOK] No se pudo parsear draftId/fileKey del public_id: {}", payload.getPublicId());
                return ResponseEntity.badRequest().build();
            }

            log.info("[PIPELINE] [WEBHOOK] Evento parseado — draftId={}, fileKey={}. Publicando MediaUploadedEvent en RabbitMQ.",
                    event.getDraftId(), event.getFileKey());
            publisher.publish(event);
            log.info("[PIPELINE] [WEBHOOK] MediaUploadedEvent publicado exitosamente.");
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("[PIPELINE] [WEBHOOK] Error procesando webhook de Cloudinary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Extrae draftId y fileKey del public_id: "drafts/{draftId}/{fileKey}"
    private MediaUploadedEvent parseEvent(CloudinaryWebhookPayloadDto payload) {
        String[] parts = payload.getPublicId().split("/");
        if (parts.length < 3 || !"drafts".equals(parts[0])) {
            return null;
        }
        try {
            UUID draftId = UUID.fromString(parts[1]);
            String fileKey = parts[2];
            return new MediaUploadedEvent(draftId, fileKey, payload.getPublicId(), payload.getResourceType(), true);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
