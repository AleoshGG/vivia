package aleosh.online.vivia.features.properties.media.messaging.consumers;

import aleosh.online.vivia.core.config.messaging.RabbitMQConfig;
import aleosh.online.vivia.features.properties.draft.messaging.events.NotificationEvent;
import aleosh.online.vivia.features.properties.draft.messaging.publishers.NotificationPublisher;
import aleosh.online.vivia.features.properties.media.domain.entities.MediaUploadSession;
import aleosh.online.vivia.features.properties.media.domain.repositories.IMediaUploadSessionRepository;
import aleosh.online.vivia.features.properties.media.messaging.events.PropertyMediaValidationResultEvent;
import aleosh.online.vivia.features.properties.media.services.IPropertyMediaStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class PropertyMediaValidationResultConsumer {

    private static final Logger log = LoggerFactory.getLogger(PropertyMediaValidationResultConsumer.class);

    private final IMediaUploadSessionRepository sessionRepository;
    private final IPropertyMediaStorageService storageService;
    private final NotificationPublisher notificationPublisher;
    private final PropertyMediaApprovalHandler approvalHandler;

    public PropertyMediaValidationResultConsumer(
            IMediaUploadSessionRepository sessionRepository,
            IPropertyMediaStorageService storageService,
            NotificationPublisher notificationPublisher,
            PropertyMediaApprovalHandler approvalHandler
    ) {
        this.sessionRepository = sessionRepository;
        this.storageService = storageService;
        this.notificationPublisher = notificationPublisher;
        this.approvalHandler = approvalHandler;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_PROPERTY_MEDIA_VALIDATION_RESULT)
    public void handle(PropertyMediaValidationResultEvent event) {
        log.info("[PropertyMedia-Result] Resultado recibido: sessionId={}, approved={}",
                event.getSessionId(), event.isApproved());

        Optional<MediaUploadSession> sessionOpt = sessionRepository.getById(event.getSessionId());
        if (sessionOpt.isEmpty()) {
            log.error("[MONITOR] event=SESSION_NOT_FOUND step=PROPERTY_MEDIA_VALIDATION_RESULT sessionId={} " +
                    "cause=La sesión ya no existe en Redis, pudo haber expirado por TTL", event.getSessionId());
            return;
        }

        MediaUploadSession session = sessionOpt.get();

        if (!event.isApproved()) {
            handleRejection(session, event.getReason());
        } else {
            approvalHandler.handle(session);
        }
    }

    private void handleRejection(MediaUploadSession session, String reason) {
        log.info("[PropertyMedia-Result] Contenido rechazado para sessionId={}: {}", session.getId(), reason);

        try {
            sessionRepository.updateStatus(session.getId(), "CONTENT_REJECTED");
        } catch (Exception e) {
            log.error("[MONITOR] event=UPDATE_STATUS_FAILED step=PROPERTY_MEDIA_REJECTION sessionId={} cause={}",
                    session.getId(), e.getMessage());
        }

        try {
            storageService.deleteBySessionId(session.getId());
        } catch (Exception e) {
            log.error("[MONITOR] event=S3_DELETE_FAILED step=PROPERTY_MEDIA_REJECTION sessionId={} cause={}",
                    session.getId(), e.getMessage());
        }

        try {
            notificationPublisher.publish(new NotificationEvent(
                    session.getLessorId(),
                    "Los medios no pudieron publicarse",
                    "El contenido multimedia no cumple con nuestras políticas. " +
                            (reason != null ? reason : ""),
                    Map.of(
                            "sessionId", session.getId().toString(),
                            "propertyId", session.getPropertyId().toString(),
                            "status", "CONTENT_REJECTED"
                    )
            ));
        } catch (Exception e) {
            log.error("[MONITOR] event=NOTIFICATION_FAILED step=PROPERTY_MEDIA_REJECTION sessionId={} cause={}",
                    session.getId(), e.getMessage());
        }

        try {
            sessionRepository.deleteById(session.getId());
        } catch (Exception e) {
            log.error("[MONITOR] event=SESSION_DELETE_FAILED step=PROPERTY_MEDIA_REJECTION sessionId={} cause={}",
                    session.getId(), e.getMessage());
        }
    }
}
