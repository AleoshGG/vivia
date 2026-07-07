package aleosh.online.vivia.features.properties.media.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con la sesión de subida de medios y las URLs prefirmadas de S3")
public class MediaUploadSessionResponseDto {

    @Schema(example = "7f8a9b0c-1d2e-4f3a-8b4c-5d6e7f8a9b0c", description = "ID de la sesión de subida")
    private UUID sessionId;

    @Schema(example = "550e8400-e29b-41d4-a716-446655440000", description = "ID de la propiedad a la que pertenecen los medios")
    private UUID propertyId;

    @Schema(example = "MEDIA_UPLOAD_PENDING", description = "Estado actual de la sesión")
    private String status;

    @Schema(example = "2026-07-06T20:00:00Z", description = "Momento en que expira la sesión")
    private Instant expiresAt;

    @Schema(description = "Lista de URLs prefirmadas para subir los archivos a S3")
    private List<PropertyMediaUploadUrlDto> uploads;
}
