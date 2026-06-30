package aleosh.online.vivia.features.users.users.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Datos para actualizar el correo electrónico del usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserEmailRequestDto {

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo es inválido")
    @Schema(description = "Nuevo correo electrónico", example = "nuevo@example.com")
    private String email;
}
