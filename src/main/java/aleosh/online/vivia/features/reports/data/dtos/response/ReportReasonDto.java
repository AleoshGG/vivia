package aleosh.online.vivia.features.reports.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Motivo de reporte registrado en el catálogo de la plataforma")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportReasonDto {

    @Schema(description = "Nombre descriptivo del motivo de reporte", example = "Información falsa")
    private String name;

    @Schema(description = "Nivel de prioridad del motivo de reporte", example = "HIGH")
    private String priority;
}
