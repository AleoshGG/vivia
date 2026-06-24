package aleosh.online.vivia.features.properties.draft.messaging.publishers;

import aleosh.online.vivia.core.config.messaging.RabbitMQConfig;
import aleosh.online.vivia.features.properties.draft.messaging.events.ContentValidationSubmitEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ContentValidationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public ContentValidationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(ContentValidationSubmitEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.QUEUE_CONTENT_VALIDATION_SUBMIT,
                event
        );
    }
}
