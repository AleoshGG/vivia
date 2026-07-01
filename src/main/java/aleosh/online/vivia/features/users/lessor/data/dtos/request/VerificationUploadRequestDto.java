package aleosh.online.vivia.features.users.lessor.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Documentos para los que se solicitan URLs de carga presignadas")
@Data
@NoArgsConstructor
public class VerificationUploadRequestDto {

    @Schema(description = "Lista de documentos con su tipo y formato de archivo")
    @NotEmpty(message = "Se requiere al menos un documento")
    @Valid
    private List<DocumentUploadEntryDto> documents;
}
