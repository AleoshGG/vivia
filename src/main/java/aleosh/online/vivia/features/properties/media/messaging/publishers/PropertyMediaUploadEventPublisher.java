package aleosh.online.vivia.features.properties.media.messaging.publishers;

import aleosh.online.vivia.core.config.messaging.RabbitMQConfig;
import aleosh.online.vivia.features.properties.media.messaging.events.PropertyMediaUploadedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PropertyMediaUploadEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public PropertyMediaUploadEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(PropertyMediaUploadedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.QUEUE_PROPERTY_MEDIA_UPLOADED,
                event
        );
    }
}
