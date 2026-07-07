package aleosh.online.vivia.features.properties.media.messaging.consumers;

import aleosh.online.vivia.features.properties.draft.messaging.events.NotificationEvent;
import aleosh.online.vivia.features.properties.draft.messaging.publishers.NotificationPublisher;
import aleosh.online.vivia.features.properties.media.domain.entities.MediaUploadSession;
import aleosh.online.vivia.features.properties.media.domain.entities.MediaUploadSessionItem;
import aleosh.online.vivia.features.properties.media.domain.repositories.IMediaUploadSessionRepository;
import aleosh.online.vivia.features.properties.media.services.IPropertyMediaStorageService;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyEntity;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyMediaEntity;
import aleosh.online.vivia.features.properties.properties.data.repositories.PropertyMediaRepository;
import aleosh.online.vivia.features.properties.properties.data.repositories.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class PropertyMediaApprovalHandler {

    private static final Logger log = LoggerFactory.getLogger(PropertyMediaApprovalHandler.class);

    private final IMediaUploadSessionRepository sessionRepository;
    private final IPropertyMediaStorageService storageService;
    private final PropertyMediaRepository propertyMediaRepository;
    private final PropertyRepository propertyRepository;
    private final NotificationPublisher notificationPublisher;

    public PropertyMediaApprovalHandler(
            IMediaUploadSessionRepository sessionRepository,
            IPropertyMediaStorageService storageService,
            PropertyMediaRepository propertyMediaRepository,
            PropertyRepository propertyRepository,
            NotificationPublisher notificationPublisher
    ) {
        this.sessionRepository = sessionRepository;
        this.storageService = storageService;
        this.propertyMediaRepository = propertyMediaRepository;
        this.propertyRepository = propertyRepository;
        this.notificationPublisher = notificationPublisher;
    }

    @Transactional
    public void handle(MediaUploadSession session) {
        log.info("[PropertyMedia-Result] Contenido aprobado para sessionId={}. Moviendo a public.", session.getId());

        Map<String, String> fileKeyToUrl = storageService.moveStagingToProperty(session.getId(), session.getPropertyId());

        Optional<PropertyEntity> propertyOpt = propertyRepository.findByIdAndDeletedAtIsNull(session.getPropertyId());
        if (propertyOpt.isEmpty()) {
            log.error("[MONITOR] event=PROPERTY_NOT_FOUND step=PROPERTY_MEDIA_APPROVAL sessionId={} propertyId={}",
                    session.getId(), session.getPropertyId());
            sessionRepository.deleteById(session.getId());
            return;
        }

        PropertyEntity property = propertyOpt.get();
        List<PropertyMediaEntity> newEntities = new ArrayList<>();

        for (MediaUploadSessionItem item : session.getMediaFiles().values()) {
            String publicUrl = fileKeyToUrl.get(item.getFileKey());
            if (publicUrl == null) {
                log.warn("[PropertyMedia-Result] No se encontró URL pública para fileKey={}, sessionId={}",
                        item.getFileKey(), session.getId());
                continue;
            }

            PropertyMediaEntity.MediaType type = item.getContentType().startsWith("video/")
                    ? PropertyMediaEntity.MediaType.VIDEO
                    : PropertyMediaEntity.MediaType.IMAGE;

            newEntities.add(PropertyMediaEntity.builder()
                    .id(UUID.randomUUID())
                    .property(property)
                    .url(publicUrl)
                    .type(type)
                    .classification(item.getClassification())
                    .build());
        }

        propertyMediaRepository.saveAll(newEntities);
        sessionRepository.updateStatus(session.getId(), "APPROVED");
        sessionRepository.deleteById(session.getId());

        try {
            notificationPublisher.publish(new NotificationEvent(
                    session.getLessorId(),
                    "Tus nuevos medios están disponibles",
                    "Los archivos que subiste han sido verificados y ya están visibles en tu propiedad.",
                    Map.of(
                            "sessionId", session.getId().toString(),
                            "propertyId", session.getPropertyId().toString(),
                            "status", "APPROVED"
                    )
            ));
        } catch (Exception e) {
            log.error("[MONITOR] event=NOTIFICATION_FAILED step=PROPERTY_MEDIA_APPROVAL sessionId={} cause={}",
                    session.getId(), e.getMessage());
        }
    }
}
