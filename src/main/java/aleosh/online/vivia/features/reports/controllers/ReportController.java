package aleosh.online.vivia.features.reports.controllers;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.reports.data.dtos.request.CreateReportRequestDto;
import aleosh.online.vivia.features.reports.services.IReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
