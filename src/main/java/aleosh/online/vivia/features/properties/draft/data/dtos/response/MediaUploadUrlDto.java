package aleosh.online.vivia.features.properties.draft.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "URL prefirmada para subir un archivo directamente a S3")
public class MediaUploadUrlDto {

    @Schema(example = "clock", description = "Identificador del archivo, igual al enviado en el manifiesto")
    private String fileKey;

    @Schema(
            example = "https://vivia-media-bucket.s3.amazonaws.com/media/staging/5af692ae-a940-4454-8cd4-dc38717a8689/clock?X-Amz-Algorithm=AWS4-HMAC-SHA256&...",
            description = "URL prefirmada de S3 para subir el archivo con PUT (válida durante expiresIn segundos)"
    )
    private String uploadUrl;

    @Schema(
            example = "media/staging/5af692ae-a940-4454-8cd4-dc38717a8689/clock",
            description = "Ruta del archivo dentro del bucket de S3"
    )
    private String storageKey;

    @Schema(example = "900", description = "Segundos de vigencia de la URL prefirmada")
    private int expiresIn;
}
