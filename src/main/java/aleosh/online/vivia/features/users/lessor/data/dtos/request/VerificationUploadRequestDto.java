package aleosh.online.vivia.features.users.lessor.data.dtos.request;

import aleosh.online.vivia.features.users.lessor.domain.objectvalues.DocumentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Tipos de documento para los que se solicitan URLs de carga")
@Data
@NoArgsConstructor
public class VerificationUploadRequestDto {

    @Schema(description = "Tipos de documento a subir", example = "[\"FRONT\", \"BACK\"]")
    @NotEmpty(message = "Se requiere al menos un tipo de documento")
    private List<DocumentType> documentTypes;
}
