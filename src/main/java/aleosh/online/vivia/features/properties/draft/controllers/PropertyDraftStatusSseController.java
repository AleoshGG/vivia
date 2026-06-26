package aleosh.online.vivia.features.properties.draft.controllers;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.features.properties.draft.domain.entities.PropertyDraft;
import aleosh.online.vivia.features.properties.draft.domain.repositories.IPropertyDraftRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@RestController
@RequestMapping("/properties/draft")
@Tag(name = "Property Draft", description = "Endpoints para seguimiento del estado del draft en tiempo real")
public class PropertyDraftStatusSseController {

    private static final Logger log = LoggerFactory.getLogger(PropertyDraftStatusSseController.class);
    private static final long SSE_TIMEOUT_MS = 30 * 60 * 1000L;
    private static final long HEARTBEAT_INTERVAL_S = 25L;
    private static final long RECONNECT_TIME_MS = 5_000L;

    private final SseEmitterRegistry sseEmitterRegistry;
    private final IPropertyDraftRepository draftRepository;
    private final TaskScheduler sseHeartbeatScheduler;

    public PropertyDraftStatusSseController(
            SseEmitterRegistry sseEmitterRegistry,
            IPropertyDraftRepository draftRepository,
            TaskScheduler sseHeartbeatScheduler
    ) {
        this.sseEmitterRegistry = sseEmitterRegistry;
        this.draftRepository = draftRepository;
        this.sseHeartbeatScheduler = sseHeartbeatScheduler;
    }

    @Operation(
            summary = "Stream de estado del draft",
            description = "Abre una conexión SSE que emite eventos en tiempo real cada vez que el " +
                    "estado del draft cambia. El cliente debe reconectarse si la conexión se interrumpe. " +
                    "Requiere autenticación como lessor.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(value = "/{draftId}/status/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamStatus(
            @PathVariable UUID draftId,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Optional<PropertyDraft> draftOpt = draftRepository.getById(draftId);
        if (draftOpt.isEmpty()) {
            log.warn("SSE solicitado para draftId={} que no existe, userId={}", draftId, userDetails.getUserId());
            SseEmitter closed = new SseEmitter(0L);
            closed.complete();
            return closed;
        }

        PropertyDraft draft = draftOpt.get();
        if (!draft.getLessorId().equals(userDetails.getUserId())) {
            log.warn("SSE denegado: draftId={} no pertenece a userId={}", draftId, userDetails.getUserId());
            SseEmitter denied = new SseEmitter(0L);
            denied.completeWithError(new SecurityException("Acceso denegado"));
            return denied;
        }

        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);

        try {
            String currentStatus = buildPayload(draftId, draft.getStatus(), draft.getUpdatedAt());
            emitter.send(SseEmitter.event()
                    .name("status_update")
                    .data(currentStatus)
                    .reconnectTime(RECONNECT_TIME_MS));
        } catch (IOException e) {
            log.warn("Error enviando estado inicial SSE para draftId={}", draftId);
        }

        sseEmitterRegistry.register(draftId, emitter);
        scheduleHeartbeat(emitter);
        log.debug("SSE registrado para draftId={}, userId={}", draftId, userDetails.getUserId());

        return emitter;
    }

    private void scheduleHeartbeat(SseEmitter emitter) {
        ScheduledFuture<?> task = sseHeartbeatScheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().comment("heartbeat"));
            } catch (Exception e) {
                // emitter ya cerrado; el onCompletion/onTimeout/onError lo eliminará del registry
            }
        }, Duration.ofSeconds(HEARTBEAT_INTERVAL_S));

        emitter.onCompletion(() -> task.cancel(false));
        emitter.onTimeout(() -> task.cancel(false));
        emitter.onError(e -> task.cancel(true));
    }

    private static String buildPayload(UUID draftId, String status, Object updatedAt) {
        return String.format(
                "{\"draftId\":\"%s\",\"status\":\"%s\",\"updatedAt\":\"%s\"}",
                draftId, status, updatedAt
        );
    }
}
