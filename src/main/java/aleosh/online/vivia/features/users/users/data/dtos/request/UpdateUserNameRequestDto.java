package aleosh.online.vivia.features.users.users.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Datos para actualizar nombre y apellidos del usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserNameRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre", example = "Alexis")
    private String name;

    @NotBlank(message = "El apellido paterno es obligatorio")
    @Schema(description = "Apellido paterno", example = "Guzmán")
    private String paternalSurname;

    @Schema(description = "Apellido materno", example = "González")
    private String maternalSurname;
}
