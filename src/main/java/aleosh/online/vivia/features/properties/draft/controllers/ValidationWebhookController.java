package aleosh.online.vivia.features.properties.draft.controllers;

import aleosh.online.vivia.features.properties.draft.data.dtos.request.ValidationResultWebhookDto;
import aleosh.online.vivia.features.properties.draft.messaging.events.AnomalyValidationResultEvent;
import aleosh.online.vivia.features.properties.draft.messaging.events.ContentValidationResultEvent;
import aleosh.online.vivia.features.properties.draft.messaging.publishers.AnomalyValidationResultPublisher;
import aleosh.online.vivia.features.properties.draft.messaging.publishers.ContentValidationResultPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/validations")
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

    @PostMapping("/content/result")
    public ResponseEntity<Void> contentResult(
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String apiKey,
            @RequestBody ValidationResultWebhookDto body
    ) {
        if (!internalApiKey.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        contentValidationResultPublisher.publish(
                new ContentValidationResultEvent(body.getDraftId(), body.isApproved(), body.getReason())
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/anomaly/result")
    public ResponseEntity<Void> anomalyResult(
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String apiKey,
            @RequestBody ValidationResultWebhookDto body
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
