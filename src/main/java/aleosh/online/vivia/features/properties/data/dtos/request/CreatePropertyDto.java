package aleosh.online.vivia.features.properties.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Modelo para la creación de una propiedad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePropertyDto {

    @Schema(description = "Título de la propiedad", example = "Departamento céntrico")
    @NotBlank(message = "El título es obligatorio")
    private String title;

    @Schema(description = "Descripción detallada", example = "Hermoso departamento amueblado de 2 recámaras...")
    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @Schema(description = "Precio mensual de renta", example = "5000.00")
    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    private Double price;

    @Schema(description = "Dirección completa", example = "Av. Siempre Viva 123")
    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @Schema(description = "Ciudad", example = "Puebla")
    @NotBlank(message = "La ciudad es obligatoria")
    private String city;

    @Schema(description = "Estado", example = "Puebla")
    @NotBlank(message = "El estado es obligatorio")
    private String state;

    @Schema(description = "Colonia o barrio", example = "Centro Histórico")
    @NotBlank(message = "La colonia o barrio es obligatorio")
    private String neighborhood;

    @Schema(description = "Tipo de departamento/propiedad", example = "Departamento")
    private String departmentType;

    @Schema(description = "Área en metros cuadrados", example = "85.5")
    @PositiveOrZero(message = "El área no puede ser negativa")
    private Double area;

    @Schema(description = "Número de habitaciones", example = "2")
    @PositiveOrZero(message = "Las habitaciones no pueden ser negativas")
    private int roomsNumber;

    @Schema(description = "Número de baños", example = "1")
    @PositiveOrZero(message = "Los baños no pueden ser negativos")
    private int bathroomsNumber;

    @Schema(description = "Lugares de estacionamiento", example = "1")
    @PositiveOrZero(message = "El estacionamiento no puede ser negativo")
    private int parkingNumber;
}
