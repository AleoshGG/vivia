package aleosh.online.vivia.features.properties.draft.messaging.consumers;

import aleosh.online.vivia.core.config.messaging.RabbitMQConfig;
import aleosh.online.vivia.features.properties.draft.controllers.DraftSsePublisher;
import aleosh.online.vivia.features.properties.draft.data.dtos.response.PublicationFailedSseDto;
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
    private final DraftSsePublisher draftSsePublisher;

    public ContentValidationResultConsumer(
            IPropertyDraftRepository draftRepository,
            IMediaStorageService mediaStorageService,
            AnomalyValidationPublisher anomalyValidationPublisher,
            NotificationPublisher notificationPublisher,
            DraftSsePublisher draftSsePublisher
    ) {
        this.draftRepository = draftRepository;
        this.mediaStorageService = mediaStorageService;
        this.anomalyValidationPublisher = anomalyValidationPublisher;
        this.notificationPublisher = notificationPublisher;
        this.draftSsePublisher = draftSsePublisher;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CONTENT_VALIDATION_RESULT)
    public void handle(ContentValidationResultEvent event) {
        log.info("[ContentValidationResult] Resultado recibido: draftId={}, approved={}",
                event.getDraftId(), event.isApproved());

        Optional<PropertyDraft> draftOpt = draftRepository.getById(event.getDraftId());
        if (draftOpt.isEmpty()) {
            // El draft expiró en Redis antes de recibir el resultado — no hay a quién notificar
            log.error("[MONITOR] event=DRAFT_NOT_FOUND step=CONTENT_VALIDATION_RESULT draftId={} " +
                    "cause=El draft ya no existe en Redis, pudo haber expirado por TTL antes de recibir el resultado de validación",
                    event.getDraftId());
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
        log.info("[ContentValidationResult] Contenido rechazado para draftId={}: {}", draft.getId(), reason);

        try {
            draftRepository.updateStatus(draft.getId(), "CONTENT_REJECTED");
        } catch (Exception e) {
            log.error("[MONITOR] event=UPDATE_STATUS_FAILED step=CONTENT_REJECTION draftId={} " +
                    "cause=No se pudo actualizar el estado del draft en Redis error={}",
                    draft.getId(), e.getMessage());
        }

        try {
            draftSsePublisher.publishFailure(draft.getId(), PublicationFailedSseDto.builder()
                    .draftId(draft.getId())
                    .status("CONTENT_REJECTED")
                    .reason(reason)
                    .build());
        } catch (Exception e) {
            log.error("[MONITOR] event=SSE_PUBLISH_FAILED step=CONTENT_REJECTION draftId={} " +
                    "cause=No se pudo publicar el evento SSE de rechazo error={}",
                    draft.getId(), e.getMessage());
        }

        try {
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
        } catch (Exception e) {
            log.error("[MONITOR] event=NOTIFICATION_FAILED step=CONTENT_REJECTION draftId={} lessorId={} " +
                    "cause=No se pudo enviar la notificación push al arrendador error={}",
                    draft.getId(), draft.getLessorId(), e.getMessage());
        }

        try {
            mediaStorageService.deleteByDraftId(draft.getId());
        } catch (Exception e) {
            log.error("[MONITOR] event=S3_DELETE_FAILED step=CONTENT_REJECTION draftId={} " +
                    "cause=No se pudieron eliminar los archivos de S3 del draft rechazado, quedan huérfanos en staging error={}",
                    draft.getId(), e.getMessage());
        }

        try {
            draftRepository.deleteById(draft.getId());
        } catch (Exception e) {
            log.error("[MONITOR] event=DRAFT_DELETE_FAILED step=CONTENT_REJECTION draftId={} " +
                    "cause=No se pudo eliminar el draft de Redis, se eliminará al vencer el TTL error={}",
                    draft.getId(), e.getMessage());
        }
    }

    private void handleApproval(PropertyDraft draft) {
        log.info("[ContentValidationResult] Contenido aprobado para draftId={}. Disparando validación de anomalías.",
                draft.getId());

        try {
            draftRepository.updateStatus(draft.getId(), "ANOMALY_VALIDATION_PENDING");
        } catch (Exception e) {
            log.error("[MONITOR] event=UPDATE_STATUS_FAILED step=CONTENT_APPROVAL draftId={} " +
                    "cause=No se pudo actualizar el estado del draft a ANOMALY_VALIDATION_PENDING error={}",
                    draft.getId(), e.getMessage());
        }

        try {
            anomalyValidationPublisher.publish(new AnomalyValidationSubmitEvent(draft));
        } catch (Exception e) {
            log.error("[MONITOR] event=ANOMALY_PUBLISH_FAILED step=CONTENT_APPROVAL draftId={} lessorId={} " +
                    "cause=No se pudo encolar el evento de validación de anomalías, el draft quedará en ANOMALY_VALIDATION_PENDING hasta que expire el TTL error={}",
                    draft.getId(), draft.getLessorId(), e.getMessage());

            try {
                notificationPublisher.publish(new NotificationEvent(
                        draft.getLessorId(),
                        "Ocurrió un problema al procesar tu propiedad",
                        "Hubo un error interno al continuar el proceso de validación. Por favor intenta publicar tu propiedad de nuevo.",
                        Map.of(
                                "draftId", draft.getId().toString(),
                                "status", "VALIDATION_ERROR"
                        )
                ));
            } catch (Exception notifEx) {
                log.error("[MONITOR] event=NOTIFICATION_FAILED step=CONTENT_APPROVAL_ERROR draftId={} lessorId={} " +
                        "cause=Fallo en cascada: no se pudo publicar el evento de anomalías NI notificar al arrendador error={}",
                        draft.getId(), draft.getLessorId(), notifEx.getMessage());
            }
        }
    }
}
