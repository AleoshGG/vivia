package aleosh.online.vivia.features.reports.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — Reportes SSE", description = "Stream de notificaciones en tiempo real de nuevos reportes")
public class AdminReportSseController {

    private final AdminReportSseRegistry adminReportSseRegistry;

    public AdminReportSseController(AdminReportSseRegistry adminReportSseRegistry) {
        this.adminReportSseRegistry = adminReportSseRegistry;
    }

    @Operation(
            summary = "Stream SSE de nuevos reportes",
            description = "El panel admin se suscribe aquí para recibir en tiempo real cada nuevo reporte de LESSEE.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        adminReportSseRegistry.register(emitter);
        return emitter;
    }
}
