package aleosh.online.vivia.features.users.admin.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

@RestController
@RequestMapping("/admin/verifications")
@Tag(name = "Admin — Verificación SSE", description = "Stream SSE para el panel de administración de verificación de identidad.")
public class VerificationAdminSseController {

    private static final Logger log = LoggerFactory.getLogger(VerificationAdminSseController.class);
    private static final long SSE_TIMEOUT_MS = 30 * 60 * 1000L;
    private static final long HEARTBEAT_INTERVAL_S = 25L;

    private final VerificationAdminSseRegistry verificationAdminSseRegistry;
    private final TaskScheduler sseHeartbeatScheduler;

    public VerificationAdminSseController(
            VerificationAdminSseRegistry verificationAdminSseRegistry,
            TaskScheduler sseHeartbeatScheduler
    ) {
        this.verificationAdminSseRegistry = verificationAdminSseRegistry;
        this.sseHeartbeatScheduler = sseHeartbeatScheduler;
    }

    @Operation(
            summary = "Stream SSE de verificaciones pendientes",
            description = "Emite un evento `verification_pending` con `LessorVerificationSummaryDto` cada vez que un arrendador sube sus documentos a S3. Requiere rol ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamVerifications() {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        verificationAdminSseRegistry.register(emitter);
        scheduleHeartbeat(emitter);
        log.debug("Admin SSE registrado para verificaciones");
        return emitter;
    }

    private void scheduleHeartbeat(SseEmitter emitter) {
        ScheduledFuture<?> task = sseHeartbeatScheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().comment("heartbeat"));
            } catch (Exception e) {
                // emitter ya cerrado; onCompletion/onTimeout/onError lo eliminarán del registry
            }
        }, Duration.ofSeconds(HEARTBEAT_INTERVAL_S));

        emitter.onCompletion(() -> task.cancel(false));
        emitter.onTimeout(() -> task.cancel(false));
        emitter.onError(e -> task.cancel(true));
    }
}
