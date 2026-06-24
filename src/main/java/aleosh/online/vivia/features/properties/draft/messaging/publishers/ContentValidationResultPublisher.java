package aleosh.online.vivia.features.properties.draft.messaging.publishers;

import aleosh.online.vivia.core.config.messaging.RabbitMQConfig;
import aleosh.online.vivia.features.properties.draft.messaging.events.ContentValidationResultEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ContentValidationResultPublisher {

    private final RabbitTemplate rabbitTemplate;

    public ContentValidationResultPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(ContentValidationResultEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.QUEUE_CONTENT_VALIDATION_RESULT,
                event
        );
    }
}
