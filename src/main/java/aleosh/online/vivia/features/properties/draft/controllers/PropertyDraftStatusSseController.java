package aleosh.online.vivia.features.properties.draft.controllers;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.features.properties.draft.domain.repositories.IPropertyDraftRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/properties/draft")
@Tag(name = "Property Draft", description = "Endpoints para seguimiento del estado del draft en tiempo real")
public class PropertyDraftStatusSseController {

    private static final Logger log = LoggerFactory.getLogger(PropertyDraftStatusSseController.class);
    // Timeout de 30 minutos: el cliente debe reconectarse si pierde la conexión
    private static final long SSE_TIMEOUT_MS = 30 * 60 * 1000L;

    private final SseEmitterRegistry sseEmitterRegistry;
    private final IPropertyDraftRepository draftRepository;

    public PropertyDraftStatusSseController(
            SseEmitterRegistry sseEmitterRegistry,
            IPropertyDraftRepository draftRepository
    ) {
        this.sseEmitterRegistry = sseEmitterRegistry;
        this.draftRepository = draftRepository;
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
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);

        // Enviar el estado actual como primer evento, para que el cliente no quede ciego
        draftRepository.getById(draftId).ifPresent(draft -> {
            // Solo el lessor dueño del draft puede hacer stream de su estado
            if (!draft.getLessorId().equals(userDetails.getUserId())) {
                emitter.completeWithError(new SecurityException("Acceso denegado"));
                return;
            }
            try {
                String currentStatus = String.format(
                        "{\"draftId\":\"%s\",\"status\":\"%s\",\"updatedAt\":\"%s\"}",
                        draftId, draft.getStatus(), draft.getUpdatedAt()
                );
                emitter.send(SseEmitter.event().name("status_update").data(currentStatus));
            } catch (IOException e) {
                log.warn("Error enviando estado inicial SSE para draftId={}", draftId);
            }
        });

        sseEmitterRegistry.register(draftId, emitter);
        log.debug("SSE registrado para draftId={}, userId={}", draftId, userDetails.getUserId());

        return emitter;
    }
}
