package aleosh.online.vivia.features.properties.draft.messaging.consumers;

import aleosh.online.vivia.core.config.messaging.RabbitMQConfig;
import aleosh.online.vivia.features.properties.draft.domain.entities.PropertyDraft;
import aleosh.online.vivia.features.properties.draft.domain.repositories.IPropertyDraftRepository;
import aleosh.online.vivia.features.properties.draft.messaging.events.AnomalyValidationSubmitEvent;
import aleosh.online.vivia.features.properties.draft.messaging.events.ContentValidationResultEvent;
import aleosh.online.vivia.features.properties.draft.messaging.events.NotificationEvent;
import aleosh.online.vivia.features.properties.draft.messaging.publishers.AnomalyValidationPublisher;
import aleosh.online.vivia.features.properties.draft.messaging.publishers.NotificationPublisher;
import aleosh.online.vivia.features.properties.draft.services.IMediaStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class ContentValidationResultConsumer {

    private static final Logger log = LoggerFactory.getLogger(ContentValidationResultConsumer.class);

    private final IPropertyDraftRepository draftRepository;
    private final IMediaStorageService mediaStorageService;
    private final AnomalyValidationPublisher anomalyValidationPublisher;
    private final NotificationPublisher notificationPublisher;

    public ContentValidationResultConsumer(
            IPropertyDraftRepository draftRepository,
            IMediaStorageService mediaStorageService,
            AnomalyValidationPublisher anomalyValidationPublisher,
            NotificationPublisher notificationPublisher
    ) {
        this.draftRepository = draftRepository;
        this.mediaStorageService = mediaStorageService;
        this.anomalyValidationPublisher = anomalyValidationPublisher;
        this.notificationPublisher = notificationPublisher;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CONTENT_VALIDATION_RESULT)
    public void handle(ContentValidationResultEvent event) {
        log.info("Resultado de validación de contenido recibido: draftId={}, approved={}",
                event.getDraftId(), event.isApproved());

        Optional<PropertyDraft> draftOpt = draftRepository.getById(event.getDraftId());
        if (draftOpt.isEmpty()) {
            log.warn("Draft {} no encontrado en Redis al procesar resultado de validación de contenido", event.getDraftId());
            return;
        }

        PropertyDraft draft = draftOpt.get();

        if (!event.isApproved()) {
            handleRejection(draft, event.getReason());
        } else {
            handleApproval(draft);
        }
    }

    private void handleRejection(PropertyDraft draft, String reason) {
        log.info("Contenido rechazado para draftId={}: {}", draft.getId(), reason);

        draftRepository.updateStatus(draft.getId(), "CONTENT_REJECTED");

        notificationPublisher.publish(new NotificationEvent(
                draft.getLessorId(),
                "Tu propiedad no pudo ser publicada",
                "El contenido multimedia no cumple con nuestras políticas. " +
                        (reason != null ? reason : ""),
                Map.of(
                        "draftId", draft.getId().toString(),
                        "status", "CONTENT_REJECTED"
                )
        ));

        mediaStorageService.deleteByDraftId(draft.getId());

        draftRepository.deleteById(draft.getId());
    }

    private void handleApproval(PropertyDraft draft) {
        log.info("Contenido aprobado para draftId={}. Disparando validación de anomalías.", draft.getId());

        draftRepository.updateStatus(draft.getId(), "ANOMALY_VALIDATION_PENDING");

        anomalyValidationPublisher.publish(new AnomalyValidationSubmitEvent(draft));
    }
}
