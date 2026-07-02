package aleosh.online.vivia.features.users.admin.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Decisión de verificación de identidad del arrendador")
@Data
@NoArgsConstructor
public class AdminUpdateVerificationRequestDto {

    @Schema(description = "Nuevo estado: VERIFIED o REJECTED")
    @NotNull(message = "El estado de verificación es requerido")
    @Pattern(regexp = "VERIFIED|REJECTED", message = "El estado debe ser VERIFIED o REJECTED")
    private String verificationStatus;

    @Schema(description = "Comentario libre del motivo de rechazo. Ignorado si el estado es VERIFIED.")
    private String comment;

    @Schema(description = "Situaciones predefinidas que motivaron el rechazo. Ignorado si el estado es VERIFIED.")
    private List<String> reasons;
}
