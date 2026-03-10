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

    @Valid
    @NotNull(message = "La credencial passkey es obligatoria")
    @Schema(description = "Credencial biométrica generada por el dispositivo")
    private PasskeyRegistrationDto passkey;
}
