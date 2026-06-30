package aleosh.online.vivia.features.users.lessee.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "Datos para actualizar la ubicación del arrendatario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLesseeUbicationDto {

    @NotNull(message = "La latitud es requerida")
    @DecimalMin(value = "-90.0", message = "La latitud debe ser mayor o igual a -90")
    @DecimalMax(value = "90.0", message = "La latitud debe ser menor o igual a 90")
    @Schema(description = "Latitud geográfica", example = "19.432608")
    private BigDecimal latitude;

    @NotNull(message = "La longitud es requerida")
    @DecimalMin(value = "-180.0", message = "La longitud debe ser mayor o igual a -180")
    @DecimalMax(value = "180.0", message = "La longitud debe ser menor o igual a 180")
    @Schema(description = "Longitud geográfica", example = "-99.133209")
    private BigDecimal longitude;
}
