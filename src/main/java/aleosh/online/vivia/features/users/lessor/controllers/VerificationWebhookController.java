package aleosh.online.vivia.features.users.lessor.controllers;

import aleosh.online.vivia.features.users.lessor.data.dtos.request.VerificationDocumentWebhookDto;
import aleosh.online.vivia.features.users.lessor.domain.objectvalues.DocumentType;
import aleosh.online.vivia.features.users.lessor.services.ILessorService;
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
@RequestMapping("/internal/verifications")
@Tag(name = "Webhooks internos — Verificación", description = "Endpoint para notificaciones de S3 cuando se sube un documento de verificación. Llamado por la Lambda vivia-s3-webhook-forwarder. Autenticación mediante header X-Internal-Api-Key.")
public class VerificationWebhookController {

    private static final Logger log = LoggerFactory.getLogger(VerificationWebhookController.class);

    private final ILessorService lessorService;
    private final String internalApiKey;
    private final String bucket;
    private final String region;

    public VerificationWebhookController(
            ILessorService lessorService,
            @Value("${vivia.internal.api-key}") String internalApiKey,
            @Value("${aws.s3.bucket}") String bucket,
            @Value("${aws.region}") String region
    ) {
        this.lessorService = lessorService;
        this.internalApiKey = internalApiKey;
        this.bucket = bucket;
        this.region = region;
    }

    @Operation(summary = "Notificación de documento de verificación subido a S3 (Lambda → Vivia)",
            description = "La Lambda vivia-s3-webhook-forwarder llama a este endpoint cuando S3 emite un ObjectCreated event para un documento de verificación. Extrae el lessorId y documentType del key con formato 'verifications/{lessorId}/{documentType}' y persiste el documento en la base de datos. Requiere header X-Internal-Api-Key.")
    @ApiResponse(responseCode = "200", description = "Documento de verificación registrado correctamente.")
    @ApiResponse(responseCode = "400", description = "El S3 key no tiene el formato esperado.")
    @ApiResponse(responseCode = "401", description = "Header X-Internal-Api-Key ausente o incorrecto.")
    @PostMapping("/s3-documents-uploaded")
    public ResponseEntity<Void> handleDocumentUploaded(
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String apiKey,
            @RequestBody VerificationDocumentWebhookDto body
    ) {
        if (!internalApiKey.equals(apiKey)) {
            log.warn("[VERIFICATION-WEBHOOK] X-Internal-Api-Key inválido — rechazando notificación.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("[VERIFICATION-WEBHOOK] Notificación recibida: bucket={}, key={}, size={}", body.getBucket(), body.getKey(), body.getSize());

        // key format: verifications/<lessorId>/<documentType>
        String[] parts = body.getKey().split("/");
        if (parts.length < 3 || !"verifications".equals(parts[0])) {
            log.warn("[VERIFICATION-WEBHOOK] Formato de key inesperado: {}", body.getKey());
            return ResponseEntity.badRequest().build();
        }

        UUID lessorId;
        try {
            lessorId = UUID.fromString(parts[1]);
        } catch (IllegalArgumentException e) {
            log.warn("[VERIFICATION-WEBHOOK] lessorId inválido en key: {}", body.getKey());
            return ResponseEntity.badRequest().build();
        }

        DocumentType documentType;
        try {
            documentType = DocumentType.valueOf(parts[2]);
        } catch (IllegalArgumentException e) {
            log.warn("[VERIFICATION-WEBHOOK] documentType inválido en key: {}", body.getKey());
            return ResponseEntity.badRequest().build();
        }

        String publicUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + body.getKey();
        log.info("[VERIFICATION-WEBHOOK] Guardando documento {} para lessor {}: {}", documentType, lessorId, publicUrl);
        lessorService.saveVerificationDocument(lessorId, documentType, publicUrl);

        return ResponseEntity.ok().build();
    }
}
