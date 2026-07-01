package aleosh.online.vivia.features.users.lessor.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Schema(description = "URLs prefirmadas para subida de documentos de verificación")
@Data
@AllArgsConstructor
public class VerificationUploadResponseDto {

    @Schema(description = "Lista de URLs por tipo de documento")
    private List<UploadUrlEntryDto> uploads;

    @Schema(description = "Segundos hasta que expiran las URLs", example = "900")
    private int expiresInSeconds;
}
