package aleosh.online.vivia.features.users.lessor.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Modelo para crear cuenta con una cuenta vinculada con Google")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterLessorGoogleDto {

    @NotBlank(message = "El ID Token de Google es requerido")
    @Schema(description = "ID de Google", example = "<ID TOKEN>")
    private String idToken;
}









