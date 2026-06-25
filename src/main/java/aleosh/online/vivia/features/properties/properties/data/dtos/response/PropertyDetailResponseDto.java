package aleosh.online.vivia.features.properties.properties.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta del endpoint GET /properties/{id}")
public class PropertyDetailResponseDto {

    @Schema(description = "Datos completos de la propiedad, incluyendo dirección, tipo, amenidades y (para LESSEE) arrendador y favorito")
    private PropertyDetailContentDto content;

    @Schema(description = "Hasta 3 imágenes de la propiedad: la primera es siempre la imagen MAIN; el resto complementa hasta el máximo")
    private List<PropertyMediaResponseDto> contentMedia;
}
