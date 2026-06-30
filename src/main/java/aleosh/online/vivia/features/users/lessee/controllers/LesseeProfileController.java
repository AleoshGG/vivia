package aleosh.online.vivia.features.users.lessee.controllers;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.UpdateLesseeUbicationDto;
import aleosh.online.vivia.features.users.lessee.services.ILesseeService;
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
@Tag(name = "Perfil arrendatario", description = "Actualización de datos del arrendatario autenticado")
public class LesseeProfileController {

    private final ILesseeService lesseeService;

    public LesseeProfileController(ILesseeService lesseeService) {
        this.lesseeService = lesseeService;
    }

    @Operation(summary = "Actualizar ubicación del arrendatario", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('LESSEE')")
    @PatchMapping("/ubication")
    public ResponseEntity<BaseResponse<Void>> updateUbication(@Valid @RequestBody UpdateLesseeUbicationDto dto, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        lesseeService.updateUbication(userDetails.getUserId(), dto);

        return new BaseResponse<Void>(true, null, "Ubicación actualizada", HttpStatus.OK).buildResponseEntity();
    }
}
