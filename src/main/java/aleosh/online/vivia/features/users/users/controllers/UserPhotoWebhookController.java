package aleosh.online.vivia.features.users.users.controllers;

import aleosh.online.vivia.features.users.users.data.dtos.request.PhotoUploadedWebhookDto;
import aleosh.online.vivia.features.users.users.services.IUserService;
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
@RequestMapping("/internal/users")
@Tag(name = "Webhooks internos — Usuarios", description = "Endpoint para notificaciones de S3 cuando se sube una foto de perfil. Llamado por la Lambda vivia-s3-webhook-forwarder. Autenticación mediante header X-Internal-Api-Key.")
public class UserPhotoWebhookController {

    private static final Logger log = LoggerFactory.getLogger(UserPhotoWebhookController.class);

    private final IUserService userService;
    private final String internalApiKey;
    private final String bucket;
    private final String region;

    public UserPhotoWebhookController(
            IUserService userService,
            @Value("${vivia.internal.api-key}") String internalApiKey,
            @Value("${aws.s3.bucket}") String bucket,
            @Value("${aws.region}") String region
    ) {
        this.userService = userService;
        this.internalApiKey = internalApiKey;
        this.bucket = bucket;
        this.region = region;
    }

    @Operation(summary = "Notificación de foto de perfil subida a S3 (Lambda → Vivia)")
    @ApiResponse(responseCode = "200", description = "Foto de perfil actualizada en la base de datos.")
    @ApiResponse(responseCode = "400", description = "El S3 key no tiene el formato esperado.")
    @ApiResponse(responseCode = "401", description = "Header X-Internal-Api-Key ausente o incorrecto.")
    @PostMapping("/s3-photo-uploaded")
    public ResponseEntity<Void> handlePhotoUploaded(
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String apiKey,
            @RequestBody PhotoUploadedWebhookDto body
    ) {
        if (!internalApiKey.equals(apiKey)) {
            log.warn("[PHOTO-WEBHOOK] X-Internal-Api-Key inválido — rechazando notificación.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("[PHOTO-WEBHOOK] Notificación recibida: bucket={}, key={}, size={}", body.getBucket(), body.getKey(), body.getSize());

        // key format: profile-photos/<userId>/avatar
        String[] parts = body.getKey().split("/");
        if (parts.length < 3 || !"profile-photos".equals(parts[0])) {
            log.warn("[PHOTO-WEBHOOK] Formato de key inesperado: {}", body.getKey());
            return ResponseEntity.badRequest().build();
        }

        UUID userId;
        try {
            userId = UUID.fromString(parts[1]);
        } catch (IllegalArgumentException e) {
            log.warn("[PHOTO-WEBHOOK] userId inválido en key: {}", body.getKey());
            return ResponseEntity.badRequest().build();
        }

        String publicUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + body.getKey();
        log.info("[PHOTO-WEBHOOK] Actualizando photoUrl de usuario {}: {}", userId, publicUrl);
        userService.updatePhotoUrl(userId, publicUrl);

        return ResponseEntity.ok().build();
    }
}
