package aleosh.online.vivia.features.users.lessor.controllers;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.VerificationUploadRequestDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.response.VerificationStatusResponseDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.response.VerificationUploadResponseDto;
import aleosh.online.vivia.features.users.lessor.services.ILessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/lessors/verifications")
@Tag(name = "Verificación de identidad", description = "Endpoints para el flujo de verificación de identidad del arrendador")
public class LessorVerificationController {

    private final ILessorService lessorService;

    public LessorVerificationController(ILessorService lessorService) {
        this.lessorService = lessorService;
    }

    @Operation(summary = "Solicitar URLs de carga de documentos de verificación",
            description = "Genera URLs prefirmadas de S3 para subir los documentos de identidad. Cambia el estado de verificación a PENDING_REVIEW.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "URLs generadas exitosamente.")
    @PreAuthorize("hasRole('LESSOR')")
    @PostMapping("/upload-urls")
    public ResponseEntity<BaseResponse<VerificationUploadResponseDto>> requestUploadUrls(
            @Valid @RequestBody VerificationUploadRequestDto dto,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        VerificationUploadResponseDto response = lessorService.requestVerificationUpload(userDetails.getUserId(), dto);

        return new BaseResponse<>(true, response, "URLs de carga generadas", HttpStatus.OK).buildResponseEntity();
    }

    @Operation(summary = "Consultar estado de verificación",
            description = "Devuelve el estado de verificación actual del arrendador autenticado.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Estado obtenido exitosamente.")
    @PreAuthorize("hasRole('LESSOR')")
    @GetMapping
    public ResponseEntity<BaseResponse<VerificationStatusResponseDto>> getVerificationStatus(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        VerificationStatusResponseDto response = lessorService.getVerificationStatus(userDetails.getUserId());

        return new BaseResponse<>(true, response, "Estado de verificación obtenido", HttpStatus.OK).buildResponseEntity();
    }

    @Operation(summary = "Reiniciar verificación",
            description = "Resetea el estado de verificación a UNVERIFIED y elimina los documentos subidos anteriormente. Solo puede ejecutarse sobre el propio perfil del arrendador autenticado.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "204", description = "Verificación reiniciada exitosamente.")
    @ApiResponse(responseCode = "403", description = "El arrendador no puede reiniciar la verificación de otro usuario.")
    @PreAuthorize("hasRole('LESSOR')")
    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> resetVerificationStatus(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if (!userDetails.getUserId().equals(id)) {
            return new BaseResponse<Void>(false, null, "No autorizado para modificar la verificación de otro arrendador", HttpStatus.FORBIDDEN)
                    .buildResponseEntity();
        }
        lessorService.resetVerificationStatus(id);

        return new BaseResponse<Void>(true, null, "Verificación reiniciada", HttpStatus.NO_CONTENT).buildResponseEntity();
    }
}
