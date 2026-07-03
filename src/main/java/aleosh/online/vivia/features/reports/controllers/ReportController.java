package aleosh.online.vivia.features.reports.controllers;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.reports.data.dtos.request.CreateReportRequestDto;
import aleosh.online.vivia.features.reports.data.dtos.response.ReportReasonDto;
import aleosh.online.vivia.features.reports.services.IReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reports")
@PreAuthorize("hasRole('LESSEE')")
@Tag(name = "Reportes", description = "Denuncias de publicaciones por arrendatarios")
public class ReportController {

    private final IReportService reportService;

    public ReportController(IReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(
            summary = "Obtener catálogo de razones de reporte",
            description = "Devuelve la lista de motivos activos disponibles para reportar una publicación. " +
                    "Usar el campo `id` al enviar el reporte.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de razones obtenida exitosamente.",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReportReasonDto.class)))
    )
    @GetMapping("/reasons")
    public ResponseEntity<BaseResponse<List<ReportReasonDto>>> getReasons() {
        List<ReportReasonDto> reasons = reportService.getReasons();
        return new BaseResponse<>(true, reasons, "Razones de reporte obtenidas", HttpStatus.OK).buildResponseEntity();
    }

    @Operation(
            summary = "Reportar una publicación",
            description = "El arrendatario denuncia una propiedad. Ejemplo: piden depósito por fuera de la plataforma.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> createReport(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateReportRequestDto dto
    ) {
        reportService.createReport(userDetails.getUserId(), dto);
        return new BaseResponse<Void>(true, null, "Reporte enviado exitosamente", HttpStatus.CREATED).buildResponseEntity();
    }
}
