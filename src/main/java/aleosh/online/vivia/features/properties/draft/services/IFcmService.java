package aleosh.online.vivia.features.properties.draft.services;

import aleosh.online.vivia.features.properties.draft.messaging.events.NotificationEvent;

public interface IFcmService {

    void send(NotificationEvent event, String fcmToken);
}
