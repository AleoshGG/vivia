package aleosh.online.vivia.features.properties.likes.data.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToggleLikeRequestDto {

    @NotNull(message = "propertyId es requerido")
    private UUID propertyId;
}
