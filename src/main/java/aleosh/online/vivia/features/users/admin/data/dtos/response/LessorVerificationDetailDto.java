package aleosh.online.vivia.features.users.admin.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Detalle completo del arrendador para revisión de identidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessorVerificationDetailDto {

    @Schema(description = "ID del arrendador")
    private UUID lessorId;

    @Schema(description = "Nombre(s) del arrendador")
    private String name;

    @Schema(description = "Apellido paterno")
    private String paternalSurname;

    @Schema(description = "Apellido materno")
    private String maternalSurname;

    @Schema(description = "Correo electrónico")
    private String email;

    @Schema(description = "URL de la foto de perfil")
    private String photoUrl;

    @Schema(description = "Número de teléfono")
    private String phoneNumber;

    @Schema(description = "Estado de verificación actual")
    private String verificationStatus;

    @Schema(description = "Fecha de registro del usuario")
    private OffsetDateTime createdAt;

    @Schema(description = "Fecha de última actualización del perfil")
    private OffsetDateTime updatedAt;

    @Schema(description = "Documentos de identidad subidos por el arrendador")
    private List<LessorDocumentResponseDto> documents;
}
