package aleosh.online.vivia.features.users.lessor.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Estado actual de verificación de identidad del arrendador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationStatusResponseDto {

    @Schema(description = "Estado de verificación: UNVERIFIED, PENDING_REVIEW, VERIFIED o REJECTED")
    private String verificationStatus;

    @Schema(description = "Razones del rechazo. Null si no hay un rechazo activo.")
    private VerificationRejectionResponseDto rejection;
}
