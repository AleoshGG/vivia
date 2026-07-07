package aleosh.online.vivia.features.properties.media.messaging.publishers;

import aleosh.online.vivia.core.config.messaging.RabbitMQConfig;
import aleosh.online.vivia.features.properties.media.messaging.events.PropertyMediaValidationResultEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PropertyMediaValidationResultPublisher {

    private final RabbitTemplate rabbitTemplate;

    public PropertyMediaValidationResultPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(PropertyMediaValidationResultEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.QUEUE_PROPERTY_MEDIA_VALIDATION_RESULT,
                event
        );
    }
}
