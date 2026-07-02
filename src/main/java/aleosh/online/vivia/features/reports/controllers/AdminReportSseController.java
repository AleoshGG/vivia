package aleosh.online.vivia.features.reports.controllers;

import aleosh.online.vivia.features.reports.data.dtos.response.PropertyReportSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        adminReportSseRegistry.register(emitter);
        return emitter;
    }
}
