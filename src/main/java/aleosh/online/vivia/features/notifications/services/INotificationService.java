package aleosh.online.vivia.features.notifications.services;

import java.util.List;

public interface INotificationService {
    void sendPropertyNotification(List<String> tokens, String lessorName, String propertyTitle);
}