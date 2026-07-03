package aleosh.online.vivia.features.reports.controllers;

import aleosh.online.vivia.features.reports.data.dtos.response.PropertyReportSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

@RestController
@RequestMapping("/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — Reportes SSE", description = "Stream de notificaciones en tiempo real de nuevos reportes")
public class AdminReportSseController {

    private static final Logger log = LoggerFactory.getLogger(AdminReportSseController.class);
    private static final long SSE_TIMEOUT_MS = 30 * 60 * 1000L;
    private static final long HEARTBEAT_INTERVAL_S = 25L;
    private static final long RECONNECT_TIME_MS = 5_000L;

    private final AdminReportSseRegistry adminReportSseRegistry;
    private final TaskScheduler sseHeartbeatScheduler;

    public AdminReportSseController(
            AdminReportSseRegistry adminReportSseRegistry,
            TaskScheduler sseHeartbeatScheduler
    ) {
        this.adminReportSseRegistry = adminReportSseRegistry;
        this.sseHeartbeatScheduler = sseHeartbeatScheduler;
    }

    @Operation(
            summary = "Stream SSE de nuevos reportes",
            description = "Conexión SSE; emite el evento `report_new` con un objeto `PropertyReportSummaryDto` cada vez que un arrendatario crea un reporte.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Evento SSE `report_new` emitido por cada nuevo reporte recibido.",
            content = @Content(schema = @Schema(implementation = PropertyReportSummaryDto.class))
    )
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);

        // Evento inicial: fuerza el flush a través de proxies y confirma al cliente que el stream está vivo
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("{}")
                    .reconnectTime(RECONNECT_TIME_MS));
        } catch (IOException e) {
            log.warn("Error enviando evento inicial SSE de reportes", e);
        }

        adminReportSseRegistry.register(emitter);
        scheduleHeartbeat(emitter);
        log.debug("Admin SSE registrado para reportes");
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
