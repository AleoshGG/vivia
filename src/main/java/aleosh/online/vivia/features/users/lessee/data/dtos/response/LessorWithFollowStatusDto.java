package aleosh.online.vivia.features.users.lessee.data.dtos.response;

import aleosh.online.vivia.features.users.lessor.data.dtos.response.LessorResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Respuesta con la información pública del arrendador y su estado de seguimiento")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessorWithFollowStatusDto {
    @Schema(description = "Datos del arrendador")
    private LessorResponseDto lessor;

    @Schema(description = "Indica si el arrendatario autenticado sigue a este arrendador", example = "true")
    private boolean isFollowing;
}
