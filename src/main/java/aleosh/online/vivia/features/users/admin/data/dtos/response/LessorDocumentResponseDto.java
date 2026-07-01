package aleosh.online.vivia.features.users.admin.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Documento de identidad subido por un arrendador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessorDocumentResponseDto {

    @Schema(description = "ID del documento", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    @Schema(description = "Tipo de documento", example = "FRONT")
    private String documentType;

    @Schema(description = "URL pública del documento en S3", example = "https://vivia-bucket.s3.us-east-1.amazonaws.com/verifications/550e8400/FRONT")
    private String uri;

    @Schema(description = "Fecha de carga", example = "2026-06-30T10:15:00Z")
    private OffsetDateTime uploadedAt;
}
