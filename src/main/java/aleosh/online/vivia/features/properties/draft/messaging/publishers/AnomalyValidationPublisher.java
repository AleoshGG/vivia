package aleosh.online.vivia.features.properties.draft.messaging.publishers;

import aleosh.online.vivia.core.config.messaging.RabbitMQConfig;
import aleosh.online.vivia.features.properties.draft.messaging.events.AnomalyValidationSubmitEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AnomalyValidationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public AnomalyValidationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(AnomalyValidationSubmitEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.QUEUE_ANOMALY_VALIDATION_SUBMIT,
                event
        );
    }
}
