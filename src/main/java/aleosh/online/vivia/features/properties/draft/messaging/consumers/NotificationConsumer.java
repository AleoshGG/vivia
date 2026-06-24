package aleosh.online.vivia.features.properties.draft.messaging.consumers;

import aleosh.online.vivia.core.config.messaging.RabbitMQConfig;
import aleosh.online.vivia.features.properties.draft.messaging.events.NotificationEvent;
import aleosh.online.vivia.features.properties.draft.services.IFcmService;
import aleosh.online.vivia.features.users.users.data.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final UserRepository userRepository;
    private final IFcmService fcmService;

    public NotificationConsumer(UserRepository userRepository, IFcmService fcmService) {
        this.userRepository = userRepository;
        this.fcmService = fcmService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NOTIFICATION)
    public void handle(NotificationEvent event) {
        log.debug("Enviando notificación push a userId={}", event.getUserId());

        Optional<String> fcmTokenOpt = userRepository.findFcmTokenByUserId(event.getUserId());

        if (fcmTokenOpt.isEmpty() || fcmTokenOpt.get().isBlank()) {
            log.warn("userId={} no tiene FCM token registrado, notificación omitida", event.getUserId());
            return;
        }

        fcmService.send(event, fcmTokenOpt.get());
    }
}
