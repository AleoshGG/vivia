package aleosh.online.vivia.features.properties.draft.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Elemento del manifiesto de archivos multimedia a subir")
public class MediaManifestItemDto {

    @Schema(example = "clock", description = "Identificador único del archivo (solo letras, números, guiones y guiones bajos)")
    @NotBlank(message = "File key is required")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "File key must contain only alphanumeric characters, hyphens, and underscores")
    private String fileKey;

    @Schema(example = "image/png", description = "Tipo MIME del archivo (ej. image/png, video/mp4)")
    @NotBlank(message = "Content type is required")
    private String contentType;

    @Schema(example = "20377", description = "Tamaño del archivo en bytes")
    @NotNull(message = "Size in bytes is required")
    @Positive(message = "Size must be greater than zero")
    private Long sizeBytes;

    @Schema(example = "INTERIOR", description = "Clasificación del archivo (ej. INTERIOR, EXTERIOR, RECORRIDO)")
    private String classification;
}
