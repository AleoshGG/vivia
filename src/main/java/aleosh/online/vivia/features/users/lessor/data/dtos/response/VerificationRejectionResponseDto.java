package aleosh.online.vivia.features.users.lessor.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "Razones del rechazo de verificación de identidad emitidas por el administrador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRejectionResponseDto {

    @Schema(description = "Comentario libre del administrador")
    private String comment;

    @Schema(description = "Situaciones predefinidas que motivaron el rechazo")
    private List<String> reasons;

    @Schema(description = "Fecha en que se registró el rechazo")
    private OffsetDateTime createdAt;
}
