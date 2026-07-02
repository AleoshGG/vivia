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

    @Schema(description = "ID del documento")
    private UUID id;

    @Schema(description = "Tipo de documento: INE_FRONT, INE_BACK o SELFIE")
    private String documentType;

    @Schema(description = "URL pública del documento en S3")
    private String uri;

    @Schema(description = "Fecha de carga del documento")
    private OffsetDateTime uploadedAt;
}
