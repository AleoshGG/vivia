package aleosh.online.vivia.features.properties.properties.data.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyMediaDto {

    @NotBlank(message = "La URL es obligatoria")
    @Size(max = 512, message = "La URL no debe exceder 512 caracteres")
    private String url;

    @NotNull(message = "El tipo de media es obligatorio")
    private MediaType type;

    @Size(max = 50, message = "La clasificación no debe exceder 50 caracteres")
    private String classification;

    public enum MediaType {
        IMAGE,
        VIDEO
    }
}
