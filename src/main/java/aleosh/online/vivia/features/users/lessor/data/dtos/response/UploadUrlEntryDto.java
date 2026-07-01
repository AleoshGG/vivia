package aleosh.online.vivia.features.users.lessor.data.dtos.response;

import aleosh.online.vivia.features.users.lessor.domain.objectvalues.DocumentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadUrlEntryDto {

    @Schema(description = "Tipo de documento", example = "FRONT")
    private DocumentType documentType;

    @Schema(description = "URL prefirmada PUT para subir el documento directamente a S3 (expira)")
    private String uploadUrl;

    @Schema(description = "URL pública permanente del documento en S3")
    private String publicUrl;
}
