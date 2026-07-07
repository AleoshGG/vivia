package aleosh.online.vivia.features.properties.media.controllers;

import aleosh.online.vivia.features.properties.media.messaging.events.PropertyMediaValidationResultEvent;
import aleosh.online.vivia.features.properties.media.messaging.publishers.PropertyMediaValidationResultPublisher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/property-media/validation")
@Tag(name = "Webhooks internos — Property Media Validation", description = "Endpoint para recibir el resultado del análisis de contenido de videos de media-sessions. Llamado por la Lambda que procesa las notificaciones SNS de Rekognition. Autenticación mediante header X-Internal-Api-Key.")
public class PropertyMediaValidationWebhookController {

    private final PropertyMediaValidationResultPublisher resultPublisher;
    private final String internalApiKey;

    public PropertyMediaValidationWebhookController(
            PropertyMediaValidationResultPublisher resultPublisher,
            @Value("${vivia.internal.api-key}") String internalApiKey
    ) {
        this.resultPublisher = resultPublisher;
        this.internalApiKey = internalApiKey;
    }

    @Operation(description = "Recibe el resultado del análisis de contenido de un video de media-session desde Rekognition vía SNS Lambda y lo publica en la cola de resultados.")
    @ApiResponse(responseCode = "200", description = "Resultado recibido y publicado en RabbitMQ.")
    @ApiResponse(responseCode = "401", description = "Header X-Internal-Api-Key ausente o incorrecto.")
    @PostMapping("/result")
    public ResponseEntity<Void> receiveResult(
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String apiKey,
            @Valid @RequestBody MediaValidationResultDto body
    ) {
        if (!internalApiKey.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        resultPublisher.publish(new PropertyMediaValidationResultEvent(
                body.getSessionId(),
                body.isApproved(),
                body.getReason()
        ));

        return ResponseEntity.ok().build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MediaValidationResultDto {

        @NotNull(message = "sessionId es obligatorio")
        private UUID sessionId;

        private boolean approved;
        private String reason;
    }
}
