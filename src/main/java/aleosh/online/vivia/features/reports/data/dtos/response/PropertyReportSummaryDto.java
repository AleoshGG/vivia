package aleosh.online.vivia.features.reports.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Resumen de un reporte emitido por un arrendatario sobre una propiedad")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyReportSummaryDto {

    @Schema(description = "ID único del reporte", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    @Schema(description = "Título de la propiedad reportada", example = "Departamento en Polanco con terraza")
    private String propertyTitle;

    @Schema(description = "Nombre completo del arrendador denunciado", example = "Carlos Mendoza López")
    private String lessorName;

    @Schema(description = "Motivo del reporte seleccionado por el arrendatario")
    private ReportReasonDto reason;

    @Schema(description = "Comentario adicional del arrendatario", example = "Las fotos no corresponden con el inmueble real")
    private String comment;

    @Schema(description = "Indica si el reporte ya fue atendido por un administrador", example = "false")
    private boolean isResolved;

    @Schema(description = "Veredicto emitido por el administrador al resolver el reporte", example = "DISMISSED")
    private String verdict;

    @Schema(description = "Fecha y hora en que se creó el reporte", example = "2026-07-02T07:37:42.053Z")
    private Instant createdAt;
}
