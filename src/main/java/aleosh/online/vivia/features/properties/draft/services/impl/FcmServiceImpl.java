package aleosh.online.vivia.features.properties.draft.services.impl;

import aleosh.online.vivia.features.properties.draft.messaging.events.NotificationEvent;
import aleosh.online.vivia.features.properties.draft.services.IFcmService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@ConditionalOnProperty(name = "fcm.enabled", havingValue = "true")
public class FcmServiceImpl implements IFcmService {

    private static final Logger log = LoggerFactory.getLogger(FcmServiceImpl.class);

    @Override
    public void send(NotificationEvent event, String fcmToken) {
        try {
            Message.Builder builder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(event.getTitle())
                            .setBody(event.getBody())
                            .build());

            if (event.getData() != null) {
                builder.putAllData(event.getData());
            }

            String messageId = FirebaseMessaging.getInstance().send(builder.build());
            log.debug("FCM enviado a userId={}: messageId={}", event.getUserId(), messageId);

        } catch (Exception e) {
            log.error("Error enviando FCM a userId={}: {}", event.getUserId(), e.getMessage(), e);
        }
    }

    @Override
    public void sendToTopic(String topic, String title, String body, Map<String, String> data) {
        try {
            Message.Builder builder = Message.builder()
                    .setTopic(topic)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());

            if (data != null) {
                builder.putAllData(data);
            }

            String messageId = FirebaseMessaging.getInstance().send(builder.build());
            log.debug("FCM topic enviado a topic={}: messageId={}", topic, messageId);

        } catch (Exception e) {
            log.error("Error enviando FCM a topic={}: {}", topic, e.getMessage(), e);
        }
    }
}
