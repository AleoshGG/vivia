package aleosh.online.vivia.features.users.lessee.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Modelo para la creación de un arrendatario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateLesseeDto {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Schema(description = "Nombre de usuario", example = "john_doe")
    private String username;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Debe ser un correo electrónico válido")
    @Schema(description = "Correo electrónico del usuario", example = "john.doe@example.com")
    private String email;
}