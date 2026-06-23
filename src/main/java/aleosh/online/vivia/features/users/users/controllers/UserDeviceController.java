package aleosh.online.vivia.features.users.users.controllers;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.users.users.data.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/me")
@Tag(name = "User Device", description = "Gestión del token de dispositivo para notificaciones push")
public class UserDeviceController {

    private final UserRepository userRepository;

    public UserDeviceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Operation(
            summary = "Registrar FCM token",
            description = "Registra o actualiza el token FCM del dispositivo del usuario autenticado. " +
                    "Debe llamarse en cada inicio de sesión o cuando FCM renueve el token.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/fcm-token")
    @Transactional
    public ResponseEntity<BaseResponse<Void>> registerFcmToken(
            @Valid @RequestBody RegisterFcmTokenRequest request,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        userRepository.updateFcmToken(userDetails.getUserId(), request.getFcmToken());

        return new BaseResponse<Void>(true, null, "FCM token registrado", HttpStatus.OK)
                .buildResponseEntity();
    }

    @Data
    @NoArgsConstructor
    public static class RegisterFcmTokenRequest {
        @NotBlank(message = "El FCM token es requerido")
        private String fcmToken;
    }
}
