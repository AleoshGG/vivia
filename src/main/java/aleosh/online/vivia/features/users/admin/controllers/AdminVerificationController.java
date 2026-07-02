package aleosh.online.vivia.features.users.admin.controllers;

import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.users.admin.data.dtos.request.AdminUpdateVerificationRequestDto;
import aleosh.online.vivia.features.users.admin.data.dtos.response.LessorVerificationDetailDto;
import aleosh.online.vivia.features.users.admin.data.dtos.response.LessorVerificationSummaryDto;
import aleosh.online.vivia.features.users.admin.services.IAdminVerificationService;
import aleosh.online.vivia.features.users.lessor.domain.objectvalues.VerificationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/lessors")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — Verificación", description = "Panel de revisión de identidad de arrendadores")
public class AdminVerificationController {

    private final IAdminVerificationService adminVerificationService;

    public AdminVerificationController(IAdminVerificationService adminVerificationService) {
        this.adminVerificationService = adminVerificationService;
    }

    @Operation(summary = "Listar arrendadores por estado de verificación", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente.")
    @GetMapping
    public ResponseEntity<BaseResponse<List<LessorVerificationSummaryDto>>> getLessorsByStatus(
            @RequestParam VerificationStatus status
    ) {
        List<LessorVerificationSummaryDto> result = adminVerificationService.getLessorsByStatus(status);
        return new BaseResponse<>(true, result, "Arrendadores obtenidos", HttpStatus.OK).buildResponseEntity();
    }

    @Operation(summary = "Ver detalle completo y documentos de identidad de un arrendador",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Detalle obtenido exitosamente.")
    @ApiResponse(responseCode = "404", description = "Arrendador no encontrado.")
    @GetMapping("/{id}/documents")
    public ResponseEntity<BaseResponse<LessorVerificationDetailDto>> getLessorDocuments(
            @PathVariable UUID id
    ) {
        LessorVerificationDetailDto result = adminVerificationService.getLessorDocuments(id);
        return new BaseResponse<>(true, result, "Detalle del arrendador obtenido", HttpStatus.OK).buildResponseEntity();
    }

    @Operation(summary = "Aprobar o rechazar la verificación de un arrendador", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Estado de verificación actualizado.")
    @ApiResponse(responseCode = "404", description = "Arrendador no encontrado.")
    @PatchMapping("/{id}/verifications")
    public ResponseEntity<BaseResponse<Void>> updateVerificationStatus(
            @PathVariable UUID id,
            @Valid @RequestBody AdminUpdateVerificationRequestDto dto
    ) {
        adminVerificationService.updateVerificationStatus(id, dto);
        return new BaseResponse<Void>(true, null, "Estado de verificación actualizado", HttpStatus.OK).buildResponseEntity();
    }
}
