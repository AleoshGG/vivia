package aleosh.online.vivia.features.users.users.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Datos para solicitar una URL de carga de foto de perfil")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoPresignRequestDto {

    @NotBlank(message = "El tipo de contenido es obligatorio")
    @Schema(description = "Tipo MIME de la imagen", example = "image/jpeg")
    private String contentType;
}
