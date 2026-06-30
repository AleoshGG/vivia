package aleosh.online.vivia.features.properties.draft.messaging.consumers;

import aleosh.online.vivia.core.config.messaging.RabbitMQConfig;
import aleosh.online.vivia.features.properties.draft.controllers.DraftSsePublisher;
import aleosh.online.vivia.features.properties.draft.data.dtos.response.PublicationFailedSseDto;
import aleosh.online.vivia.features.properties.draft.data.dtos.response.PublishedPropertySseDto;
import aleosh.online.vivia.features.properties.draft.domain.entities.PropertyDraft;
import aleosh.online.vivia.features.properties.draft.domain.repositories.IPropertyDraftRepository;
import aleosh.online.vivia.features.properties.draft.messaging.events.AnomalyValidationResultEvent;
import aleosh.online.vivia.features.properties.draft.messaging.events.NotificationEvent;
import aleosh.online.vivia.features.properties.draft.messaging.publishers.NotificationPublisher;
import aleosh.online.vivia.features.properties.draft.services.IAnalysisStorageService;
import aleosh.online.vivia.features.properties.draft.services.IMediaStorageService;
import aleosh.online.vivia.features.properties.draft.services.IPropertyPublicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class AnomalyValidationResultConsumer {

    private static final Logger log = LoggerFactory.getLogger(AnomalyValidationResultConsumer.class);

    private final IPropertyDraftRepository draftRepository;
    private final IPropertyPublicationService propertyPublicationService;
    private final IAnalysisStorageService analysisStorageService;
    private final IMediaStorageService mediaStorageService;
    private final NotificationPublisher notificationPublisher;
    private final DraftSsePublisher draftSsePublisher;

    public AnomalyValidationResultConsumer(
            IPropertyDraftRepository draftRepository,
            IPropertyPublicationService propertyPublicationService,
            IAnalysisStorageService analysisStorageService,
            IMediaStorageService mediaStorageService,
            NotificationPublisher notificationPublisher,
            DraftSsePublisher draftSsePublisher
    ) {
        this.draftRepository = draftRepository;
        this.propertyPublicationService = propertyPublicationService;
        this.analysisStorageService = analysisStorageService;
        this.mediaStorageService = mediaStorageService;
        this.notificationPublisher = notificationPublisher;
        this.draftSsePublisher = draftSsePublisher;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ANOMALY_VALIDATION_RESULT)
    public void handle(AnomalyValidationResultEvent event) {
        log.info("Resultado de validación de anomalías recibido: draftId={}, approved={}",
                event.getDraftId(), event.isApproved());

        Optional<PropertyDraft> draftOpt = draftRepository.getById(event.getDraftId());
        if (draftOpt.isEmpty()) {
            log.warn("Draft {} no encontrado en Redis al procesar resultado de validación de anomalías", event.getDraftId());
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
        log.info("Anomalía detectada en draftId={}: {}", draft.getId(), reason);

        draftRepository.updateStatus(draft.getId(), "ANOMALY_REJECTED");

        draftSsePublisher.publishFailure(draft.getId(), PublicationFailedSseDto.builder()
                .draftId(draft.getId())
                .status("ANOMALY_REJECTED")
                .reason(reason)
                .build());

        // Guardar en Firestore ANTES de eliminar (conservar datos para análisis)
        analysisStorageService.saveRejectedDraft(draft, reason);

        mediaStorageService.deleteByDraftId(draft.getId());

        notificationPublisher.publish(new NotificationEvent(
                draft.getLessorId(),
                "Tu propiedad no pudo ser publicada",
                "Detectamos una anomalía en los datos de tu propiedad. " +
                        (reason != null ? reason : ""),
                Map.of(
                        "draftId", draft.getId().toString(),
                        "status", "ANOMALY_REJECTED"
                )
        ));

        draftRepository.deleteById(draft.getId());
    }

    private void handleApproval(PropertyDraft draft) {
        log.info("Propiedad {} aprobada. Moviendo media a public y publicando en PostgreSQL.", draft.getId());

        mediaStorageService.moveStagingToPublic(draft.getId());

        PublishedPropertySseDto publishedDto = propertyPublicationService.publish(draft);

        draftRepository.updateStatus(draft.getId(), "PUBLISHED");

        draftSsePublisher.publishSuccess(draft.getId(), publishedDto);

        notificationPublisher.publish(new NotificationEvent(
                draft.getLessorId(),
                "¡Tu propiedad ya está publicada!",
                "Los arrendatarios ya pueden ver y contactarte por \"" + draft.getTitle() + "\".",
                Map.of(
                        "draftId", draft.getId().toString(),
                        "propertyId", draft.getId().toString(),
                        "status", "PUBLISHED"
                )
        ));

        draftRepository.deleteById(draft.getId());
    }
}
