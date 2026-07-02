package aleosh.online.vivia.features.properties.draft.services.impl;

import aleosh.online.vivia.features.properties.draft.messaging.events.NotificationEvent;
import aleosh.online.vivia.features.properties.draft.services.IFcmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@ConditionalOnProperty(name = "fcm.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpFcmServiceImpl implements IFcmService {

    private static final Logger log = LoggerFactory.getLogger(NoOpFcmServiceImpl.class);

    @Override
    public void send(NotificationEvent event, String fcmToken) {
        log.info("[NO-OP FCM] userId={} title=\"{}\" body=\"{}\"",
                event.getUserId(), event.getTitle(), event.getBody());
    }

    @Override
    public void sendToTopic(String topic, String title, String body, Map<String, String> data) {
        log.info("[NO-OP FCM] topic={} title=\"{}\" body=\"{}\"", topic, title, body);
    }
}
