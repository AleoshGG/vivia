package aleosh.online.vivia.features.properties.draft.messaging.consumers;

import aleosh.online.vivia.core.config.messaging.RabbitMQConfig;
import aleosh.online.vivia.features.properties.draft.messaging.events.ContentValidationSubmitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ContentValidationSubmitConsumer {

    private static final Logger log = LoggerFactory.getLogger(ContentValidationSubmitConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CONTENT_VALIDATION_SUBMIT)
    public void handle(ContentValidationSubmitEvent event) {
        log.info("[PIPELINE] [DEBUG] ContentValidationSubmitConsumer recibió evento para draftId={}",
                event.getDraft().getId());
        log.info("[PIPELINE] [DEBUG] *** SIMULANDO invocación al servicio de moderación de contenido ***");
        log.info("[PIPELINE] [DEBUG] Draft title='{}', lessorId={}, totalFiles={}",
                event.getDraft().getTitle(),
                event.getDraft().getLessorId(),
                event.getDraft().getTotalFiles());
        log.info("[PIPELINE] [DEBUG] Para completar el pipeline, el servicio de moderación debe llamar a: POST /internal/validations/content/result");
    }
}
