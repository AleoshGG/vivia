package aleosh.online.vivia.features.properties.media.messaging.consumers;

import aleosh.online.vivia.core.config.messaging.RabbitMQConfig;
import aleosh.online.vivia.features.properties.draft.services.IContentModerationService;
import aleosh.online.vivia.features.properties.media.domain.entities.MediaUploadSession;
import aleosh.online.vivia.features.properties.media.domain.entities.MediaUploadSessionItem;
import aleosh.online.vivia.features.properties.media.messaging.events.PropertyMediaValidationResultEvent;
import aleosh.online.vivia.features.properties.media.messaging.events.PropertyMediaValidationSubmitEvent;
import aleosh.online.vivia.features.properties.media.messaging.publishers.PropertyMediaValidationResultPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class PropertyMediaValidationSubmitConsumer {

    private static final Logger log = LoggerFactory.getLogger(PropertyMediaValidationSubmitConsumer.class);

    private final IContentModerationService contentModerationService;
    private final PropertyMediaValidationResultPublisher resultPublisher;

    public PropertyMediaValidationSubmitConsumer(
            IContentModerationService contentModerationService,
            PropertyMediaValidationResultPublisher resultPublisher
    ) {
        this.contentModerationService = contentModerationService;
        this.resultPublisher = resultPublisher;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_PROPERTY_MEDIA_VALIDATION_SUBMIT)
    public void handle(PropertyMediaValidationSubmitEvent event) {
        MediaUploadSession session = event.getSession();
        log.info("[PropertyMedia-Validation] Iniciando análisis para sessionId={}, propertyId={}",
                session.getId(), session.getPropertyId());

        Collection<MediaUploadSessionItem> items = session.getMediaFiles().values();

        List<MediaUploadSessionItem> images = items.stream()
                .filter(m -> m.getContentType().startsWith("image/"))
                .toList();

        List<MediaUploadSessionItem> videos = items.stream()
                .filter(m -> m.getContentType().startsWith("video/"))
                .toList();

        for (MediaUploadSessionItem image : images) {
            boolean approved;
            try {
                approved = contentModerationService.moderateImage(image.getStorageKey());
            } catch (Exception e) {
                log.error("[PropertyMedia-Validation] Error analizando imagen {} para sessionId={}: {}",
                        image.getStorageKey(), session.getId(), e.getMessage());
                publishResult(session.getId(), false, "Error analyzing image: " + image.getFileKey());
                return;
            }

            if (!approved) {
                log.info("[PropertyMedia-Validation] Imagen {} rechazada. sessionId={}", image.getFileKey(), session.getId());
                publishResult(session.getId(), false, "Image contains inappropriate content: " + image.getFileKey());
                return;
            }
        }

        if (videos.isEmpty()) {
            log.info("[PropertyMedia-Validation] Todas las imágenes aprobadas para sessionId={}. Sin videos.", session.getId());
            publishResult(session.getId(), true, null);
            return;
        }

        // Prefijo "media-session:" para que la Lambda SNS enrute al endpoint correcto (/internal/property-media/validation/result)
        String jobTag = "media-session:" + session.getId();
        for (MediaUploadSessionItem video : videos) {
            try {
                contentModerationService.submitVideoModeration(video.getStorageKey(), jobTag);
                log.info("[PropertyMedia-Validation] Video {} enviado a Rekognition async. jobTag={}", video.getFileKey(), jobTag);
            } catch (Exception e) {
                log.error("[PropertyMedia-Validation] Error enviando video {} a Rekognition para sessionId={}: {}",
                        video.getStorageKey(), session.getId(), e.getMessage());
                publishResult(session.getId(), false, "Error submitting video for analysis: " + video.getFileKey());
                return;
            }
        }

        log.info("[PropertyMedia-Validation] {} video(s) enviados a análisis async para sessionId={}.",
                videos.size(), session.getId());
    }

    private void publishResult(java.util.UUID sessionId, boolean approved, String reason) {
        resultPublisher.publish(new PropertyMediaValidationResultEvent(sessionId, approved, reason));
    }
}
