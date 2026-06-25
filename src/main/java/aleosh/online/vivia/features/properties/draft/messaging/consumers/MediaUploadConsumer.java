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
        Optional<PropertyDraft> draftOpt = draftRepository.getById(event.getDraftId());
        if (draftOpt.isEmpty()) {
            log.warn("Draft {} no encontrado en Redis — ignorando MediaUploadedEvent.", event.getDraftId());
            return;
        }

        PropertyDraft draft = draftOpt.get();

        if (!event.isSuccess()) {
            log.warn("Upload fallido para draftId={}, fileKey={}.", event.getDraftId(), event.getFileKey());
            draftRepository.updateStatus(event.getDraftId(), "MEDIA_UPLOAD_FAILED");
            draftRepository.deleteById(event.getDraftId());
            return;
        }

        int uploadedFiles = draftRepository.incrementUploadedFiles(event.getDraftId());

        if (uploadedFiles >= draft.getTotalFiles()) {
            draftRepository.updateStatus(event.getDraftId(), "CONTENT_VALIDATION_PENDING");
            contentValidationPublisher.publish(new ContentValidationSubmitEvent(draft));
        }
    }
}
