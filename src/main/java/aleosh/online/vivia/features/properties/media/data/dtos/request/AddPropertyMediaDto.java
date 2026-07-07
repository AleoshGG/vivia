package aleosh.online.vivia.features.properties.media.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para agregar nuevos medios a una propiedad publicada")
public class AddPropertyMediaDto {

    @NotNull(message = "propertyId es obligatorio")
    @Schema(example = "550e8400-e29b-41d4-a716-446655440000", description = "ID de la propiedad a la que se agregarán los medios")
    private UUID propertyId;

    @NotEmpty(message = "mediaManifest no puede estar vacío")
    @Valid
    @Schema(description = "Lista de archivos a subir; ninguno puede tener classification = MAIN")
    private List<PropertyMediaManifestItemDto> mediaManifest;
}
