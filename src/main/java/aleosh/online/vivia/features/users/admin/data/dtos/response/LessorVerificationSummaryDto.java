package aleosh.online.vivia.features.users.admin.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(description = "Resumen de un arrendador pendiente de revisión")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessorVerificationSummaryDto {

    @Schema(description = "ID del arrendador", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID lessorId;

    @Schema(description = "Nombre completo", example = "Carlos Gómez")
    private String name;

    @Schema(description = "Correo electrónico", example = "carlos@gmail.com")
    private String email;

    @Schema(description = "Estado de verificación", example = "PENDING_REVIEW")
    private String verificationStatus;
}
