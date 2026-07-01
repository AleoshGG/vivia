package aleosh.online.vivia.features.users.admin.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Decisión de verificación de identidad del arrendador")
@Data
@NoArgsConstructor
public class AdminUpdateVerificationRequestDto {

    @Schema(description = "Nuevo estado, solo VERIFIED o REJECTED", example = "VERIFIED")
    @NotNull(message = "El estado de verificación es requerido")
    @Pattern(regexp = "VERIFIED|REJECTED", message = "El estado debe ser VERIFIED o REJECTED")
    private String verificationStatus;
}
