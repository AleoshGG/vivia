package aleosh.online.vivia.features.users.lessee.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Este es modelo para poder crear un usuario Lessee desde su cuenta de Google")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterLesseeGoogleDto {
    @NotBlank(message = "El idToken que provee Google es obligatorio")
    @Schema(description = "ID Token que provee la API de Google", example = "<ID TOKEN>")
    private String idToken;
}
