package aleosh.online.vivia.features.users.admin.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Token FCM del navegador del administrador")
@Data
@NoArgsConstructor
public class AdminFcmTokenRequestDto {

    @Schema(description = "Token FCM emitido por Firebase para el navegador actual")
    @NotBlank(message = "El FCM token es requerido")
    private String fcmToken;
}
