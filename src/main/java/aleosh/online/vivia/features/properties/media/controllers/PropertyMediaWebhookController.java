package aleosh.online.vivia.features.properties.media.controllers;

import aleosh.online.vivia.features.properties.media.data.dtos.request.PropertyMediaUploadWebhookDto;
import aleosh.online.vivia.features.properties.media.messaging.events.PropertyMediaUploadedEvent;
import aleosh.online.vivia.features.properties.media.messaging.publishers.PropertyMediaUploadEventPublisher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/property-media")
@Tag(name = "Webhooks internos — Property Media", description = "Endpoint para notificaciones de S3 cuando un archivo de medios adicionales es subido. Llamado por la Lambda vivia-property-media-webhook-forwarder. Autenticación mediante header X-Internal-Api-Key.")
public class PropertyMediaWebhookController {

    private static final Logger log = LoggerFactory.getLogger(PropertyMediaWebhookController.class);

    private final PropertyMediaUploadEventPublisher publisher;
    private final String internalApiKey;

    public PropertyMediaWebhookController(
            PropertyMediaUploadEventPublisher publisher,
            @Value("${vivia.internal.api-key}") String internalApiKey
    ) {
        this.publisher = publisher;
        this.internalApiKey = internalApiKey;
    }

    @Operation(description = "Webhook interno llamado por la Lambda cuando un archivo de medios de propiedad llega a S3.")
    @ApiResponse(responseCode = "200", description = "Evento procesado y publicado en RabbitMQ.")
    @ApiResponse(responseCode = "400", description = "El S3 key no tiene el formato esperado.")
    @ApiResponse(responseCode = "401", description = "Header X-Internal-Api-Key ausente o incorrecto.")
    @PostMapping("/s3-uploaded")
    public ResponseEntity<Void> handleS3Upload(
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String apiKey,
            @RequestBody PropertyMediaUploadWebhookDto body
    ) {
        if (!internalApiKey.equals(apiKey)) {
            log.warn("[Property-Media-Webhook] X-Internal-Api-Key inválido — rechazando notificación.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("[Property-Media-Webhook] Notificación recibida: bucket={}, key={}, size={}",
                body.getBucket(), body.getKey(), body.getSize());

        // key format: media/property-staging/<sessionId>/<fileKey>
        String[] parts = body.getKey().split("/");
        if (parts.length < 4 || !"media".equals(parts[0]) || !"property-staging".equals(parts[1])) {
            log.warn("[Property-Media-Webhook] Formato de key inesperado: {}", body.getKey());
            return ResponseEntity.badRequest().build();
        }

        UUID sessionId;
        try {
            sessionId = UUID.fromString(parts[2]);
        } catch (IllegalArgumentException e) {
            log.warn("[Property-Media-Webhook] sessionId inválido en key: {}", body.getKey());
            return ResponseEntity.badRequest().build();
        }

        String fileKey = parts[3];
        log.info("[Property-Media-Webhook] Publicando PropertyMediaUploadedEvent: sessionId={}, fileKey={}", sessionId, fileKey);
        publisher.publish(new PropertyMediaUploadedEvent(sessionId, fileKey, body.getKey(), true));

        return ResponseEntity.ok().build();
    }
}
