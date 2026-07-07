package aleosh.online.vivia.features.properties.media.data.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para cambiar la imagen principal de una propiedad")
public class ChangeMainImageDto {

    @NotNull(message = "main_image_id es obligatorio")
    @JsonProperty("main_image_id")
    @Schema(example = "b1e2c3d4-5f6a-4b7c-8d9e-0a1b2c3d4e5f", description = "ID de la imagen que actualmente tiene classification = MAIN")
    private UUID mainImageId;

    @NotNull(message = "new_main_image_id es obligatorio")
    @JsonProperty("new_main_image_id")
    @Schema(example = "c2f3d4e5-6a7b-4c8d-9e0f-1a2b3c4d5e6f", description = "ID de la imagen que pasará a ser MAIN")
    private UUID newMainImageId;
}
