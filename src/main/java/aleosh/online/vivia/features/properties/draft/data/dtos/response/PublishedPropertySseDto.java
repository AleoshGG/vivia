package aleosh.online.vivia.features.properties.draft.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Payload del evento SSE 'publication_success'. Contiene el resumen de la propiedad recién publicada para añadirla directamente a la lista en la app.")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishedPropertySseDto {

    @Schema(description = "ID de la propiedad publicada", example = "a3f8c1d2-4b56-7890-abcd-ef1234567890")
    private UUID id;

    @Schema(description = "URL pública de la imagen principal de la propiedad", example = "https://vivia-bucket.s3.us-east-1.amazonaws.com/media/public/a3f8c1d2-4b56-7890-abcd-ef1234567890/portada.jpg")
    private String mainImageUrl;

    @Schema(description = "Título de la propiedad", example = "Departamento moderno en Polanco con balcón")
    private String title;

    @Schema(description = "Precio mensual listado en MXN", example = "18500.00")
    private BigDecimal listedPrice;

    @Schema(description = "Superficie en metros cuadrados", example = "85.50")
    private BigDecimal areaM2;

    @Schema(description = "Número de recámaras", example = "2")
    private Integer bedrooms;

    @Schema(description = "Número de baños", example = "1.5")
    private BigDecimal bathrooms;

    @Schema(description = "Nombre del tipo de propiedad", example = "Departamento")
    private String propertyTypeName;
}
