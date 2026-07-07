package aleosh.online.vivia.features.users.admin.controllers;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.users.admin.data.dtos.request.AdminFcmTokenRequestDto;
import aleosh.online.vivia.features.users.admin.services.IAdminFcmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/fcm")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — FCM", description = "Gestión de tokens FCM para notificaciones web del panel de administración.")
@ConditionalOnProperty(name = "fcm.enabled", havingValue = "true")
public class AdminFcmController {

    private final IAdminFcmService adminFcmService;

    public AdminFcmController(IAdminFcmService adminFcmService) {
        this.adminFcmService = adminFcmService;
    }

    @Operation(
            summary = "Registrar token FCM del admin",
            description = "Guarda el token FCM del navegador del admin y lo suscribe a los topics " +
                    "'admin-verifications' y 'admin-reports' para recibir notificaciones push del panel.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/subscribe")
    public ResponseEntity<BaseResponse<Void>> subscribe(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AdminFcmTokenRequestDto body
    ) {
        adminFcmService.subscribe(userDetails.getUserId(), body.getFcmToken());
        return new BaseResponse<Void>(true, null, "Token FCM registrado", HttpStatus.OK).buildResponseEntity();
    }

    @Operation(
            summary = "Eliminar token FCM del admin",
            description = "Borra el token FCM guardado y lo desuscribe de los topics del panel. Llamar al cerrar sesión.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/subscribe")
    public ResponseEntity<BaseResponse<Void>> unsubscribe(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AdminFcmTokenRequestDto body
    ) {
        adminFcmService.unsubscribe(userDetails.getUserId(), body.getFcmToken());
        return new BaseResponse<Void>(true, null, "Token FCM eliminado", HttpStatus.OK).buildResponseEntity();
    }
}
