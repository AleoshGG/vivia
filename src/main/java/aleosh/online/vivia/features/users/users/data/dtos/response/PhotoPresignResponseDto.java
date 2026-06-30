package aleosh.online.vivia.features.users.users.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "URL prefirmada y URL pública final de la foto de perfil")
@Data
@AllArgsConstructor
public class PhotoPresignResponseDto {

    @Schema(description = "URL prefirmada PUT para subir la imagen directamente a S3 (expira)", example = "https://vivia-media-bucket.s3.us-east-1.amazonaws.com/profile-photos/uuid/avatar?X-Amz-Algorithm=AWS4-HMAC-SHA256&...")
    private String presignedUrl;

    @Schema(description = "URL pública permanente donde quedará la foto", example = "https://vivia-media-bucket.s3.us-east-1.amazonaws.com/profile-photos/uuid/avatar")
    private String photoUrl;

    @Schema(description = "Segundos hasta que expira la presigned URL", example = "300")
    private int expiresInSeconds;
}
