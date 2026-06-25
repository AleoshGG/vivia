package aleosh.online.vivia.features.properties.draft.controllers;

import aleosh.online.vivia.features.properties.draft.data.dtos.request.MediaUploadWebhookDto;
import aleosh.online.vivia.features.properties.draft.messaging.events.MediaUploadedEvent;
import aleosh.online.vivia.features.properties.draft.messaging.publishers.MediaUploadEventPublisher;
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
@RequestMapping("/internal/media")
@Tag(name = "Webhooks internos — Media", description = "Endpoint para notificaciones de S3 cuando un archivo es subido. Llamado por la Lambda vivia-s3-webhook-forwarder. Autenticación mediante header X-Internal-Api-Key.")
public class MediaUploadWebhookController {

    private static final Logger log = LoggerFactory.getLogger(MediaUploadWebhookController.class);

    private final MediaUploadEventPublisher publisher;
    private final String internalApiKey;

    public MediaUploadWebhookController(
            MediaUploadEventPublisher publisher,
            @Value("${vivia.internal.api-key}") String internalApiKey
    ) {
        this.publisher = publisher;
        this.internalApiKey = internalApiKey;
    }

    @Operation(
            summary = "Notificación de archivo subido a S3 (Lambda → Vivia)",
            description = "La Lambda vivia-s3-webhook-forwarder llama a este endpoint cuando S3 emite un ObjectCreated event. Valida X-Internal-Api-Key, extrae draftId y fileKey del S3 key con formato 'media/staging/{draftId}/{fileKey}', y publica un MediaUploadedEvent en RabbitMQ."
    )
    @ApiResponse(responseCode = "200", description = "Evento procesado y publicado en RabbitMQ.")
    @ApiResponse(responseCode = "400", description = "El S3 key no tiene el formato esperado.")
    @ApiResponse(responseCode = "401", description = "Header X-Internal-Api-Key ausente o incorrecto.")
    @PostMapping("/s3-uploaded")
    public ResponseEntity<Void> handleS3Upload(
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String apiKey,
            @RequestBody MediaUploadWebhookDto body
    ) {
        if (!internalApiKey.equals(apiKey)) {
            log.warn("[S3-WEBHOOK] X-Internal-Api-Key inválido — rechazando notificación.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("[S3-WEBHOOK] Notificación recibida: bucket={}, key={}, size={}", body.getBucket(), body.getKey(), body.getSize());

        // key format: media/staging/<draftId>/<fileKey>
        String[] parts = body.getKey().split("/");
        if (parts.length < 4 || !"media".equals(parts[0]) || !"staging".equals(parts[1])) {
            log.warn("[S3-WEBHOOK] Formato de key inesperado: {}", body.getKey());
            return ResponseEntity.badRequest().build();
        }

        UUID draftId;
        try {
            draftId = UUID.fromString(parts[2]);
        } catch (IllegalArgumentException e) {
            log.warn("[S3-WEBHOOK] draftId inválido en key: {}", body.getKey());
            return ResponseEntity.badRequest().build();
        }

        String fileKey = parts[3];
        log.info("[S3-WEBHOOK] Publicando MediaUploadedEvent: draftId={}, fileKey={}", draftId, fileKey);
        publisher.publish(new MediaUploadedEvent(draftId, fileKey, body.getKey(), null, true));

        return ResponseEntity.ok().build();
    }
}
