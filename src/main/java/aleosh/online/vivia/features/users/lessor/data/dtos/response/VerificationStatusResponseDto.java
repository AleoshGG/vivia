package aleosh.online.vivia.features.users.lessor.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "Estado actual de verificación del arrendador")
@Data
@AllArgsConstructor
public class VerificationStatusResponseDto {

    @Schema(description = "Estado de verificación", example = "PENDING_REVIEW")
    private String verificationStatus;
}
