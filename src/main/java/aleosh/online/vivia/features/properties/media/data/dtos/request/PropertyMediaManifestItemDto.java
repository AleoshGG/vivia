package aleosh.online.vivia.features.properties.media.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Elemento del manifiesto de medios a subir")
public class PropertyMediaManifestItemDto {

    @NotBlank(message = "fileKey es obligatorio")
    @Pattern(regexp = "[a-zA-Z0-9_-]+", message = "fileKey solo puede contener letras, números, guiones y guiones bajos")
    @Schema(example = "clock", description = "Identificador único del archivo dentro de la sesión")
    private String fileKey;

    @NotBlank(message = "contentType es obligatorio")
    @Pattern(regexp = "image/.+|video/.+", message = "contentType debe ser un tipo de imagen o video válido")
    @Schema(example = "image/png", description = "MIME type del archivo")
    private String contentType;

    @Positive(message = "sizeBytes debe ser mayor a cero")
    @Schema(example = "20377", description = "Tamaño del archivo en bytes")
    private long sizeBytes;

    @NotBlank(message = "classification es obligatoria")
    @Schema(example = "INTERIOR", description = "Clasificación del medio: INTERIOR, EXTERIOR, BATHROOM, BEDROOM, etc. No puede ser MAIN.")
    private String classification;
}
