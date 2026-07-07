package aleosh.online.vivia.features.properties.media.messaging.consumers;

import aleosh.online.vivia.core.config.messaging.RabbitMQConfig;
import aleosh.online.vivia.features.properties.media.domain.entities.MediaUploadSession;
import aleosh.online.vivia.features.properties.media.domain.repositories.IMediaUploadSessionRepository;
import aleosh.online.vivia.features.properties.media.messaging.events.PropertyMediaUploadedEvent;
import aleosh.online.vivia.features.properties.media.messaging.events.PropertyMediaValidationSubmitEvent;
import aleosh.online.vivia.features.properties.media.messaging.publishers.PropertyMediaValidationSubmitPublisher;
import aleosh.online.vivia.features.properties.media.services.IPropertyMediaStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PropertyMediaUploadConsumer {

    private static final Logger log = LoggerFactory.getLogger(PropertyMediaUploadConsumer.class);

    private final IMediaUploadSessionRepository sessionRepository;
    private final PropertyMediaValidationSubmitPublisher validationSubmitPublisher;
    private final IPropertyMediaStorageService storageService;

    public PropertyMediaUploadConsumer(
            IMediaUploadSessionRepository sessionRepository,
            PropertyMediaValidationSubmitPublisher validationSubmitPublisher,
            IPropertyMediaStorageService storageService
    ) {
        this.sessionRepository = sessionRepository;
        this.validationSubmitPublisher = validationSubmitPublisher;
        this.storageService = storageService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_PROPERTY_MEDIA_UPLOADED)
    public void handle(PropertyMediaUploadedEvent event) {
        Optional<MediaUploadSession> sessionOpt = sessionRepository.getById(event.getSessionId());
        if (sessionOpt.isEmpty()) {
            log.warn("[PropertyMedia] Sesión {} no encontrada en Redis — ignorando evento.", event.getSessionId());
            return;
        }

        MediaUploadSession session = sessionOpt.get();

        if (!event.isSuccess()) {
            log.warn("[PropertyMedia] Upload fallido para sessionId={}, fileKey={}.", event.getSessionId(), event.getFileKey());
            sessionRepository.updateStatus(event.getSessionId(), "MEDIA_UPLOAD_FAILED");
            storageService.deleteBySessionId(event.getSessionId());
            sessionRepository.deleteById(event.getSessionId());
            return;
        }

        int uploadedFiles = sessionRepository.incrementUploadedFiles(event.getSessionId());
        log.info("[PropertyMedia] Archivo {} recibido ({}/{}) para sessionId={}",
                event.getFileKey(), uploadedFiles, session.getTotalFiles(), event.getSessionId());

        if (uploadedFiles >= session.getTotalFiles()) {
            sessionRepository.updateStatus(event.getSessionId(), "CONTENT_VALIDATION_PENDING");
            validationSubmitPublisher.publish(new PropertyMediaValidationSubmitEvent(session));
            log.info("[PropertyMedia] Fan-in completo para sessionId={}. Enviando a validación de contenido.", event.getSessionId());
        }
    }
}
