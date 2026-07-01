package aleosh.online.vivia.features.users.lessor.data.dtos.request;

import aleosh.online.vivia.features.users.lessor.domain.objectvalues.DocumentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Entrada de documento a subir con su tipo de contenido")
@Data
@NoArgsConstructor
public class DocumentUploadEntryDto {

    @Schema(description = "Tipo de documento", example = "FRONT")
    @NotNull(message = "El tipo de documento es obligatorio")
    private DocumentType documentType;

    @Schema(description = "Tipo MIME del archivo a subir", example = "image/jpeg")
    @NotBlank(message = "El tipo de contenido es obligatorio")
    private String contentType;
}
