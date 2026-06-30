package aleosh.online.vivia.features.properties.draft.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(description = "Payload del evento SSE 'publication_failed'. Contiene el motivo del rechazo emitido por la IA para renderizar la alerta correspondiente en la app.")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicationFailedSseDto {

    @Schema(description = "ID del draft rechazado", example = "a3f8c1d2-4b56-7890-abcd-ef1234567890")
    private UUID draftId;

    @Schema(
            description = "Tipo de rechazo. CONTENT_REJECTED: el multimedia no cumple con las políticas. ANOMALY_REJECTED: se detectó una anomalía en los datos de la propiedad.",
            example = "CONTENT_REJECTED",
            allowableValues = {"CONTENT_REJECTED", "ANOMALY_REJECTED"}
    )
    private String status;

    @Schema(description = "Razón del rechazo emitida por la IA", example = "Las imágenes contienen marcas de agua o logotipos de terceros que no están permitidos.")
    private String reason;
}
