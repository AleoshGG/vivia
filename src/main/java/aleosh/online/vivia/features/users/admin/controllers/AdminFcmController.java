package aleosh.online.vivia.features.users.admin.controllers;

import com.google.firebase.messaging.FirebaseMessaging;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/fcm")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — FCM", description = "Gestión de tokens FCM para notificaciones web del panel de administración.")
@ConditionalOnProperty(name = "fcm.enabled", havingValue = "true")
public class AdminFcmController {

    private static final Logger log = LoggerFactory.getLogger(AdminFcmController.class);
    private static final String TOPIC = "admin-verifications";

    @Operation(
            summary = "Suscribir token FCM al topic de verificaciones",
            description = "Registra el token FCM del navegador admin al topic 'admin-verifications' para recibir notificaciones push cuando un arrendador sube sus documentos.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/subscribe")
    public ResponseEntity<Void> subscribe(@Valid @RequestBody FcmSubscribeRequest body) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(List.of(body.getFcmToken()), TOPIC);
            log.info("Token FCM suscrito al topic '{}' correctamente", TOPIC);
        } catch (Exception e) {
            log.error("Error suscribiendo token FCM al topic '{}': {}", TOPIC, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Desuscribir token FCM del topic de verificaciones",
            description = "Elimina el token FCM del navegador del topic 'admin-verifications'. Llamar al cerrar sesión.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/subscribe")
    public ResponseEntity<Void> unsubscribe(@Valid @RequestBody FcmSubscribeRequest body) {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(List.of(body.getFcmToken()), TOPIC);
            log.info("Token FCM desuscrito del topic '{}' correctamente", TOPIC);
        } catch (Exception e) {
            log.error("Error desuscribiendo token FCM del topic '{}': {}", TOPIC, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }

    @Data
    @NoArgsConstructor
    public static class FcmSubscribeRequest {
        @NotBlank
        private String fcmToken;
    }
}
