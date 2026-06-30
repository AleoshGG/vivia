package aleosh.online.vivia.features.auth.controllers;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.auth.data.dtos.request.UpdatePasswordRequestDto;
import aleosh.online.vivia.features.auth.services.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/me")
@Tag(name = "Auth perfil", description = "Gestión de credenciales del usuario autenticado")
public class AuthProfileController {

    private final IAuthService authService;

    public AuthProfileController(IAuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Cambiar contraseña", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/password")
    public ResponseEntity<BaseResponse<Void>> updatePassword(@Valid @RequestBody UpdatePasswordRequestDto dto, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        authService.updatePassword(userDetails.getUserId(), dto);

        return new BaseResponse<Void>(true, null, "Contraseña actualizada", HttpStatus.OK).buildResponseEntity();
    }
}
