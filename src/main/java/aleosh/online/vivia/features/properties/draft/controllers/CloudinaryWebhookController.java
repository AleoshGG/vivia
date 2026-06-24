package aleosh.online.vivia.features.properties.draft.controllers;

import aleosh.online.vivia.features.properties.draft.data.dtos.request.CloudinaryWebhookPayloadDto;
import aleosh.online.vivia.features.properties.draft.messaging.events.MediaUploadedEvent;
import aleosh.online.vivia.features.properties.draft.messaging.publishers.MediaUploadEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal")
@Tag(name = "Webhooks internos — Cloudinary", description = "Endpoint exclusivo para Cloudinary. Notifica a Vivia cuando el cliente móvil termina de subir un archivo con upload firmado. No debe ser llamado por clientes móviles ni por otros servicios.")
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

    @Operation(
            summary = "Notificación de upload completado (Cloudinary → Vivia)",
            description = "Cloudinary llama a este endpoint cuando el cliente sube un archivo con upload firmado. Valida la firma del webhook (SHA1 de rawBody + X-Cld-Timestamp + cloudinary.api-secret), parsea el public_id con formato 'drafts/{draftId}/{fileKey}' y publica un MediaUploadedEvent en la cola 'vivia.media.file.uploaded'. Cuando todos los archivos del draft están subidos, el consumer avanza el status a CONTENT_VALIDATION_PENDING. Solo se procesan notificaciones con notification_type='upload'; cualquier otro tipo se ignora con 200."
    )
    @ApiResponse(responseCode = "200", description = "Evento procesado y publicado en RabbitMQ.")
    @ApiResponse(responseCode = "400", description = "El public_id no tiene el formato esperado 'drafts/{draftId}/{fileKey}'.")
    @ApiResponse(responseCode = "401", description = "Firma inválida o ausente. Headers X-Cld-Signature o X-Cld-Timestamp faltantes o incorrectos.")
    @ApiResponse(responseCode = "500", description = "Error interno al procesar el payload o publicar el evento.")
    @PostMapping(value = "/media/uploaded", consumes = "application/json")
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
