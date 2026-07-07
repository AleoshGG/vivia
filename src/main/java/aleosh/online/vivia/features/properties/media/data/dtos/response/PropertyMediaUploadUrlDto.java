package aleosh.online.vivia.features.properties.media.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "URL prefirmada para subir un archivo directamente a S3")
public class PropertyMediaUploadUrlDto {

    @Schema(example = "clock", description = "Identificador del archivo, igual al enviado en el manifiesto")
    private String fileKey;

    @Schema(example = "https://vivia-media-bucket.s3.amazonaws.com/media/property-staging/7f8a9b0c-.../clock?X-Amz-Algorithm=AWS4-HMAC-SHA256&...", description = "URL prefirmada de S3 para subir el archivo con PUT")
    private String uploadUrl;

    @Schema(example = "media/property-staging/7f8a9b0c-1d2e-4f3a-8b4c-5d6e7f8a9b0c/clock", description = "Ruta del archivo dentro del bucket de S3")
    private String storageKey;

    @Schema(example = "900", description = "Segundos de vigencia de la URL prefirmada")
    private int expiresInSeconds;
}
