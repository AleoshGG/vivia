package aleosh.online.vivia.features.properties.draft.services;

import aleosh.online.vivia.features.properties.draft.messaging.events.NotificationEvent;

import java.util.Map;

public interface IFcmService {

    void send(NotificationEvent event, String fcmToken);

    void sendToTopic(String topic, String title, String body, Map<String, String> data);
}
