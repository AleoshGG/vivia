package aleosh.online.vivia.features.users.lessor.controllers;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.UpdateLessorPhoneRequestDto;
import aleosh.online.vivia.features.users.lessor.services.ILessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/me")
@Tag(name = "Perfil arrendador", description = "Actualización de datos del arrendador autenticado")
public class LessorProfileController {

    private final ILessorService lessorService;

    public LessorProfileController(ILessorService lessorService) {
        this.lessorService = lessorService;
    }

    @Operation(summary = "Actualizar teléfono del arrendador", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('LESSOR')")
    @PatchMapping("/phone")
    public ResponseEntity<BaseResponse<Void>> updatePhone(@Valid @RequestBody UpdateLessorPhoneRequestDto dto, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        lessorService.updatePhoneNumber(userDetails.getUserId(), dto);

        return new BaseResponse<Void>(true, null, "Teléfono actualizado", HttpStatus.OK).buildResponseEntity();
    }
}
