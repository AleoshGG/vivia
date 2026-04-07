package aleosh.online.vivia.features.users.lessor.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Modelo para la creación de un arrendador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateLessorDto {
    @Schema(description = "Nombres del usuario", example = "Alexis Leonel")
    private String firstName;

    @Schema(description = "Apellidos del usuario", example = "Guzmán González")
    private String lastName;

    @Schema(description = "Nombre de la empresa único", example = "Rentas Ws")
    private String companyName;

    @Schema(description = "Contraseña", example = "password123")
    private String password;

    @Schema(description = "Número de teléfono", example = "5564234321")
    private String phoneNumber;
}
