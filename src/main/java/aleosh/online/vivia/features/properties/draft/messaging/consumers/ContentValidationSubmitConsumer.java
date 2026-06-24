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
        log.info("Draft {} enviado a validación de contenido.", event.getDraft().getId());
    }
}
