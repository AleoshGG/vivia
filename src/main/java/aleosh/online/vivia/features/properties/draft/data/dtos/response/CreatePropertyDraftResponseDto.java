package aleosh.online.vivia.features.properties.draft.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import aleosh.online.vivia.features.properties.draft.data.dtos.response.MediaUploadUrlDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con el ID del borrador y las URLs prefirmadas para subir los archivos multimedia")
public class CreatePropertyDraftResponseDto {

    @Schema(example = "5af692ae-a940-4454-8cd4-dc38717a8689", description = "ID único del borrador creado")
    private UUID draftId;

    @Schema(example = "PENDING_MEDIA", description = "Estado actual del borrador")
    private String status;

    @Schema(example = "2026-06-26T01:51:29.272050939Z", description = "Fecha y hora de expiración del borrador (ISO-8601 UTC)")
    private String expiresAt;

    @Schema(description = "Lista de URLs prefirmadas para subir cada archivo directamente a S3")
    private List<MediaUploadUrlDto> uploads;
}
