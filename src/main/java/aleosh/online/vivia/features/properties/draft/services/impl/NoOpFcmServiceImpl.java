package aleosh.online.vivia.features.properties.draft.services.impl;

import aleosh.online.vivia.features.properties.draft.messaging.events.NotificationEvent;
import aleosh.online.vivia.features.properties.draft.services.IFcmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpFcmServiceImpl implements IFcmService {

    private static final Logger log = LoggerFactory.getLogger(NoOpFcmServiceImpl.class);

    @Override
    public void send(NotificationEvent event, String fcmToken) {
        log.info("[NO-OP FCM] userId={} title=\"{}\" body=\"{}\"",
                event.getUserId(), event.getTitle(), event.getBody());
    }
}
