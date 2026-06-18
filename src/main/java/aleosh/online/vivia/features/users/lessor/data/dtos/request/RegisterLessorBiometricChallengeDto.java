package aleosh.online.vivia.features.users.lessor.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Datos para solicitar el desafío de registro biométrico de arrendador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterLessorBiometricChallengeDto {

    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    @Schema(description = "Correo electrónico del usuario", example = "juan@example.com")
    private String email;

    @NotBlank(message = "El nombre es requerido")
    @Schema(description = "Nombre del arrendador", example = "Juan")
    private String name;

    @NotBlank(message = "El apellido paterno es requerido")
    @Schema(description = "Apellido paterno", example = "Pérez")
    private String paternalSurname;

    @NotBlank(message = "El apellido materno es requerido")
    @Schema(description = "Apellido materno", example = "García")
    private String maternalSurname;

    @NotBlank(message = "El número de telefono es requerido")
    @Schema(description = "Número de Teléfono", example = "9274577845")
    private String phoneNumber;
}
