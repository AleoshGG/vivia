package aleosh.online.vivia.features.notifications.services.impl;

import aleosh.online.vivia.features.notifications.services.INotificationService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements INotificationService {

    @Override
    @Async
    public void sendPropertyNotification(List<String> tokens, String lessorName, String propertyTitle) {
        if (tokens == null || tokens.isEmpty()) {
            return;
        }

        Notification notification = Notification.builder()
                .setTitle("¡Nuevo departamento de " + lessorName + "!")
                .setBody("Se ha publicado: " + propertyTitle)
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(notification)
                .build();

        try {
            System.out.println("Intentando enviar notificación a " + tokens.size() + " dispositivos...");
            FirebaseMessaging.getInstance().sendMulticastAsync(message).addListener(() -> {
                System.out.println("Firebase procesó la petición de forma asíncrona.");
            }, Runnable::run);
        } catch (Exception e) {
            System.err.println("Error grave enviando a Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }
}