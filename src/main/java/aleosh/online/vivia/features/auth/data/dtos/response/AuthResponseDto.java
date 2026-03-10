package aleosh.online.vivia.features.auth.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Modelo de respuesta del loggeo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    @Schema(description = "JSON Web Token", example = "token")
    private String token;
}

