package aleosh.online.vivia.features.users.admin.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Resumen de un arrendador en la lista de revisión de identidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessorVerificationSummaryDto {

    @Schema(description = "ID del arrendador")
    private UUID lessorId;

    @Schema(description = "Nombre completo del arrendador")
    private String name;

    @Schema(description = "Correo electrónico del arrendador")
    private String email;

    @Schema(description = "Estado de verificación actual")
    private String verificationStatus;

    @Schema(description = "Fecha del último documento subido")
    private OffsetDateTime lastUploadedAt;
}
