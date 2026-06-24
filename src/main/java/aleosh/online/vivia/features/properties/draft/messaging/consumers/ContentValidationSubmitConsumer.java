package aleosh.online.vivia.features.properties.draft.messaging.consumers;

import aleosh.online.vivia.core.config.messaging.RabbitMQConfig;
import aleosh.online.vivia.features.properties.draft.domain.entities.PropertyDraft;
import aleosh.online.vivia.features.properties.draft.domain.entities.PropertyDraftMedia;
import aleosh.online.vivia.features.properties.draft.messaging.events.ContentValidationResultEvent;
import aleosh.online.vivia.features.properties.draft.messaging.events.ContentValidationSubmitEvent;
import aleosh.online.vivia.features.properties.draft.messaging.publishers.ContentValidationResultPublisher;
import aleosh.online.vivia.features.properties.draft.services.IContentModerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class ContentValidationSubmitConsumer {

    private static final Logger log = LoggerFactory.getLogger(ContentValidationSubmitConsumer.class);

    private final IContentModerationService contentModerationService;
    private final ContentValidationResultPublisher contentValidationResultPublisher;

    public ContentValidationSubmitConsumer(
            IContentModerationService contentModerationService,
            ContentValidationResultPublisher contentValidationResultPublisher
    ) {
        this.contentModerationService = contentModerationService;
        this.contentValidationResultPublisher = contentValidationResultPublisher;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CONTENT_VALIDATION_SUBMIT)
    public void handle(ContentValidationSubmitEvent event) {
        PropertyDraft draft = event.getDraft();
        log.info("[ContentValidation] Iniciando análisis para draftId={}", draft.getId());

        Collection<PropertyDraftMedia> media = draft.getMediaFiles().values();
        List<PropertyDraftMedia> images = media.stream()
                .filter(m -> m.getContentType().startsWith("image/"))
                .toList();
        List<PropertyDraftMedia> videos = media.stream()
                .filter(m -> m.getContentType().startsWith("video/"))
                .toList();

        // Analizar imágenes síncronamente
        for (PropertyDraftMedia image : images) {
            boolean approved;
            try {
                approved = contentModerationService.moderateImage(image.getStorageKey());
            } catch (Exception e) {
                log.error("[ContentValidation] Error analizando imagen {} para draftId={}: {}",
                        image.getStorageKey(), draft.getId(), e.getMessage());
                publishResult(draft.getId(), false, "Error al analizar imagen: " + image.getFileKey());
                return;
            }

            if (!approved) {
                log.info("[ContentValidation] Imagen {} rechazada. draftId={}", image.getFileKey(), draft.getId());
                publishResult(draft.getId(), false, "Imagen con contenido inapropiado: " + image.getFileKey());
                return;
            }
        }

        // Si no hay videos, publicar aprobación inmediata
        if (videos.isEmpty()) {
            log.info("[ContentValidation] Todas las imágenes aprobadas para draftId={}. Sin videos.", draft.getId());
            publishResult(draft.getId(), true, null);
            return;
        }

        // Enviar videos a análisis asíncrono con Rekognition
        for (PropertyDraftMedia video : videos) {
            try {
                contentModerationService.submitVideoModeration(video.getStorageKey(), draft.getId());
            } catch (Exception e) {
                log.error("[ContentValidation] Error enviando video {} a Rekognition para draftId={}: {}",
                        video.getStorageKey(), draft.getId(), e.getMessage());
                publishResult(draft.getId(), false, "Error al enviar video a análisis: " + video.getFileKey());
                return;
            }
        }

        log.info("[ContentValidation] {} video(s) enviados a análisis async para draftId={}. Esperando resultado vía SNS.",
                videos.size(), draft.getId());
    }

    private void publishResult(java.util.UUID draftId, boolean approved, String reason) {
        contentValidationResultPublisher.publish(new ContentValidationResultEvent(draftId, approved, reason));
    }
}
