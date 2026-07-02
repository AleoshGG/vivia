package aleosh.online.vivia.features.reports.controllers;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.reports.data.dtos.request.ReportVerdictRequestDto;
import aleosh.online.vivia.features.reports.data.dtos.response.PropertyReportDetailDto;
import aleosh.online.vivia.features.reports.data.dtos.response.PropertyReportSummaryDto;
import aleosh.online.vivia.features.reports.services.IReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — Reportes", description = "Panel de gestión de denuncias sobre publicaciones")
public class AdminReportController {

    private final IReportService reportService;

    public AdminReportController(IReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(
            summary = "Listar reportes pendientes",
            description = "Devuelve todos los reportes aún sin resolver, ordenados del más reciente.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente.")
    @GetMapping
    public ResponseEntity<BaseResponse<List<PropertyReportSummaryDto>>> getPendingReports() {
        List<PropertyReportSummaryDto> result = reportService.getPendingReports();
        return new BaseResponse<>(true, result, "Reportes pendientes obtenidos", HttpStatus.OK).buildResponseEntity();
    }

    @Operation(
            summary = "Ver detalle de un reporte",
            description = "Muestra la información completa del reporte, incluyendo la propiedad si aún existe.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Detalle obtenido exitosamente.")
    @ApiResponse(responseCode = "404", description = "Reporte no encontrado.")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PropertyReportDetailDto>> getReportDetail(@PathVariable UUID id) {
        PropertyReportDetailDto result = reportService.getReportDetail(id);
        return new BaseResponse<>(true, result, "Detalle del reporte obtenido", HttpStatus.OK).buildResponseEntity();
    }

    @Operation(
            summary = "Historial de reportes por arrendador",
            description = "Lista todos los reportes (resueltos y pendientes) asociados a un arrendador específico.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente.")
    @GetMapping("/history/{lessorId}")
    public ResponseEntity<BaseResponse<List<PropertyReportSummaryDto>>> getHistoryByLessor(@PathVariable UUID lessorId) {
        List<PropertyReportSummaryDto> result = reportService.getHistoryByLessor(lessorId);
        return new BaseResponse<>(true, result, "Historial de reportes obtenido", HttpStatus.OK).buildResponseEntity();
    }

    @Operation(
            summary = "Aplicar veredicto a un reporte",
            description = "El admin emite un veredicto: descarta, elimina la publicación o suspende la cuenta del arrendador.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Veredicto aplicado exitosamente.")
    @ApiResponse(responseCode = "404", description = "Reporte no encontrado.")
    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> applyVerdict(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReportVerdictRequestDto dto
    ) {
        reportService.applyVerdict(id, userDetails.getUserId(), dto);
        return new BaseResponse<Void>(true, null, "Veredicto aplicado", HttpStatus.OK).buildResponseEntity();
    }
}
