package aleosh.online.vivia.core.config.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "vivia.properties";

    public static final String QUEUE_MEDIA_UPLOADED = "vivia.media.file.uploaded";
    public static final String QUEUE_CONTENT_VALIDATION_SUBMIT = "vivia.validation.content.submit";
    public static final String QUEUE_CONTENT_VALIDATION_RESULT = "vivia.validation.content.result";
    public static final String QUEUE_ANOMALY_VALIDATION_SUBMIT = "vivia.validation.anomaly.submit";
    public static final String QUEUE_ANOMALY_VALIDATION_RESULT = "vivia.validation.anomaly.result";
    public static final String QUEUE_NOTIFICATION = "vivia.notification.send";
    public static final String QUEUE_DLQ = "vivia.dlq";

    @Bean
    public TopicExchange propertiesExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue mediaUploadedQueue() {
        return QueueBuilder.durable(QUEUE_MEDIA_UPLOADED)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_DLQ)
                .build();
    }

    @Bean
    public Queue contentValidationSubmitQueue() {
        return QueueBuilder.durable(QUEUE_CONTENT_VALIDATION_SUBMIT)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_DLQ)
                .build();
    }

    @Bean
    public Queue contentValidationResultQueue() {
        return QueueBuilder.durable(QUEUE_CONTENT_VALIDATION_RESULT)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_DLQ)
                .build();
    }

    @Bean
    public Queue anomalyValidationSubmitQueue() {
        return QueueBuilder.durable(QUEUE_ANOMALY_VALIDATION_SUBMIT)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_DLQ)
                .build();
    }

    @Bean
    public Queue anomalyValidationResultQueue() {
        return QueueBuilder.durable(QUEUE_ANOMALY_VALIDATION_RESULT)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_DLQ)
                .build();
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(QUEUE_NOTIFICATION)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_DLQ)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(QUEUE_DLQ).build();
    }

    @Bean
    public Binding mediaUploadedBinding(Queue mediaUploadedQueue, TopicExchange propertiesExchange) {
        return BindingBuilder.bind(mediaUploadedQueue).to(propertiesExchange).with(QUEUE_MEDIA_UPLOADED);
    }

    @Bean
    public Binding contentValidationSubmitBinding(Queue contentValidationSubmitQueue, TopicExchange propertiesExchange) {
        return BindingBuilder.bind(contentValidationSubmitQueue).to(propertiesExchange).with(QUEUE_CONTENT_VALIDATION_SUBMIT);
    }

    @Bean
    public Binding contentValidationResultBinding(Queue contentValidationResultQueue, TopicExchange propertiesExchange) {
        return BindingBuilder.bind(contentValidationResultQueue).to(propertiesExchange).with(QUEUE_CONTENT_VALIDATION_RESULT);
    }

    @Bean
    public Binding anomalyValidationSubmitBinding(Queue anomalyValidationSubmitQueue, TopicExchange propertiesExchange) {
        return BindingBuilder.bind(anomalyValidationSubmitQueue).to(propertiesExchange).with(QUEUE_ANOMALY_VALIDATION_SUBMIT);
    }

    @Bean
    public Binding anomalyValidationResultBinding(Queue anomalyValidationResultQueue, TopicExchange propertiesExchange) {
        return BindingBuilder.bind(anomalyValidationResultQueue).to(propertiesExchange).with(QUEUE_ANOMALY_VALIDATION_RESULT);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange propertiesExchange) {
        return BindingBuilder.bind(notificationQueue).to(propertiesExchange).with(QUEUE_NOTIFICATION);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
