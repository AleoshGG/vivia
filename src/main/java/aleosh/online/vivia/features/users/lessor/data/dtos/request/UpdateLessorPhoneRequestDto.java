package aleosh.online.vivia.features.users.lessor.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Datos para actualizar el número de teléfono del arrendador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLessorPhoneRequestDto {

    @NotBlank(message = "El número de teléfono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "El teléfono debe tener entre 10 y 15 dígitos")
    @Schema(description = "Número de teléfono", example = "5512345678")
    private String phoneNumber;
}
