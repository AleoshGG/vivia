package aleosh.online.vivia.features.properties.draft.controllers;

import aleosh.online.vivia.features.properties.draft.data.dtos.request.ValidationResultWebhookDto;
import aleosh.online.vivia.features.properties.draft.messaging.events.AnomalyValidationResultEvent;
import aleosh.online.vivia.features.properties.draft.messaging.events.ContentValidationResultEvent;
import aleosh.online.vivia.features.properties.draft.messaging.publishers.AnomalyValidationResultPublisher;
import aleosh.online.vivia.features.properties.draft.messaging.publishers.ContentValidationResultPublisher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/validations")
@Tag(name = "Webhooks internos — Validaciones", description = "Endpoints exclusivos para los microservicios de validación. Reciben el resultado del análisis de contenido y detección de anomalías de un draft. No deben ser llamados por clientes móviles. Autenticación mediante header X-Internal-Api-Key.")
public class ValidationWebhookController {

    private final ContentValidationResultPublisher contentValidationResultPublisher;
    private final AnomalyValidationResultPublisher anomalyValidationResultPublisher;
    private final String internalApiKey;

    public ValidationWebhookController(
            ContentValidationResultPublisher contentValidationResultPublisher,
            AnomalyValidationResultPublisher anomalyValidationResultPublisher,
            @Value("${vivia.internal.api-key}") String internalApiKey
    ) {
        this.contentValidationResultPublisher = contentValidationResultPublisher;
        this.anomalyValidationResultPublisher = anomalyValidationResultPublisher;
        this.internalApiKey = internalApiKey;
    }

    @Operation(
            summary = "Resultado de validación de contenido (servicio externo → Vivia)",
            description = "El servicio de validación de contenido llama a este endpoint con el resultado del análisis del draft (coherencia, veracidad y políticas de la plataforma). Publica un ContentValidationResultEvent en la cola 'vivia.validation.content.result'. Si approved=true, el draft avanza a ANOMALY_VALIDATION_PENDING. Si approved=false, el draft queda en CONTENT_VALIDATION_REJECTED, se eliminan los archivos de Cloudinary y se notifica al arrendador con el reason. Requiere header X-Internal-Api-Key."
    )
    @ApiResponse(responseCode = "200", description = "Resultado recibido y publicado en RabbitMQ correctamente.")
    @ApiResponse(responseCode = "401", description = "Header X-Internal-Api-Key ausente o incorrecto.")
    @PostMapping("/content/result")
    public ResponseEntity<Void> contentResult(
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String apiKey,
            @Valid @RequestBody ValidationResultWebhookDto body
    ) {
        if (!internalApiKey.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        contentValidationResultPublisher.publish(
                new ContentValidationResultEvent(body.getDraftId(), body.isApproved(), body.getReason())
        );
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Resultado de validación de anomalías (servicio externo → Vivia)",
            description = "El servicio de detección de anomalías llama a este endpoint con el resultado del análisis visual del draft (contenido inapropiado, imágenes duplicadas, irregularidades). Publica un AnomalyValidationResultEvent en la cola 'vivia.validation.anomaly.result'. Si approved=true, el draft avanza a APPROVED, se persiste como propiedad publicada y se notifica al arrendador. Si approved=false, el draft queda en ANOMALY_VALIDATION_REJECTED, se eliminan los archivos de Cloudinary y se notifica al arrendador con el reason. Requiere header X-Internal-Api-Key."
    )
    @ApiResponse(responseCode = "200", description = "Resultado recibido y publicado en RabbitMQ correctamente.")
    @ApiResponse(responseCode = "401", description = "Header X-Internal-Api-Key ausente o incorrecto.")
    @PostMapping("/anomaly/result")
    public ResponseEntity<Void> anomalyResult(
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String apiKey,
            @Valid @RequestBody ValidationResultWebhookDto body
    ) {
        if (!internalApiKey.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        anomalyValidationResultPublisher.publish(
                new AnomalyValidationResultEvent(body.getDraftId(), body.isApproved(), body.getReason())
        );
        return ResponseEntity.ok().build();
    }
}
