package aleosh.online.vivia.features.properties.draft.messaging.consumers;

import aleosh.online.vivia.core.config.messaging.RabbitMQConfig;
import aleosh.online.vivia.features.properties.draft.domain.entities.PropertyDraft;
import aleosh.online.vivia.features.properties.draft.domain.repositories.IPropertyDraftRepository;
import aleosh.online.vivia.features.properties.draft.messaging.events.ContentValidationSubmitEvent;
import aleosh.online.vivia.features.properties.draft.messaging.events.MediaUploadedEvent;
import aleosh.online.vivia.features.properties.draft.messaging.publishers.ContentValidationPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MediaUploadConsumer {

    private static final Logger log = LoggerFactory.getLogger(MediaUploadConsumer.class);

    private final IPropertyDraftRepository draftRepository;
    private final ContentValidationPublisher contentValidationPublisher;

    public MediaUploadConsumer(
            IPropertyDraftRepository draftRepository,
            ContentValidationPublisher contentValidationPublisher
    ) {
        this.draftRepository = draftRepository;
        this.contentValidationPublisher = contentValidationPublisher;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_MEDIA_UPLOADED)
    public void handle(MediaUploadedEvent event) {
        log.info("[PIPELINE] [CONSUMER] MediaUploadedEvent recibido: draftId={}, fileKey={}, success={}",
                event.getDraftId(), event.getFileKey(), event.isSuccess());

        Optional<PropertyDraft> draftOpt = draftRepository.getById(event.getDraftId());
        if (draftOpt.isEmpty()) {
            log.warn("[PIPELINE] [CONSUMER] Draft {} no encontrado en Redis (TTL expirado o inválido) — ignorando evento.",
                    event.getDraftId());
            return;
        }

        PropertyDraft draft = draftOpt.get();
        log.info("[PIPELINE] [CONSUMER] Draft encontrado en Redis: draftId={}, totalFiles={}, status={}",
                draft.getId(), draft.getTotalFiles(), draft.getStatus());

        if (!event.isSuccess()) {
            log.warn("[PIPELINE] [CONSUMER] Upload fallido para draftId={}, fileKey={}. Marcando como MEDIA_UPLOAD_FAILED y eliminando draft.",
                    event.getDraftId(), event.getFileKey());
            draftRepository.updateStatus(event.getDraftId(), "MEDIA_UPLOAD_FAILED");
            draftRepository.deleteById(event.getDraftId());
            return;
        }

        int uploadedFiles = draftRepository.incrementUploadedFiles(event.getDraftId());
        log.info("[PIPELINE] [CONSUMER] Progreso de uploads: {}/{} para draftId={}",
                uploadedFiles, draft.getTotalFiles(), event.getDraftId());

        if (uploadedFiles >= draft.getTotalFiles()) {
            log.info("[PIPELINE] [CONSUMER] *** TODOS LOS ARCHIVOS SUBIDOS *** draftId={}. Actualizando status a CONTENT_VALIDATION_PENDING.",
                    event.getDraftId());
            draftRepository.updateStatus(event.getDraftId(), "CONTENT_VALIDATION_PENDING");
            log.info("[PIPELINE] [CONSUMER] Publicando ContentValidationSubmitEvent para draftId={}.", event.getDraftId());
            contentValidationPublisher.publish(new ContentValidationSubmitEvent(draft));
            log.info("[PIPELINE] [CONSUMER] ContentValidationSubmitEvent publicado. Pipeline de media upload COMPLETADO para draftId={}.",
                    event.getDraftId());
        }
    }
}
