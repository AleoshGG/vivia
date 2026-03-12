package aleosh.online.vivia.features.notifications.services.impl;

import aleosh.online.vivia.features.notifications.services.INotificationService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
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

        System.out.println("Intentando enviar notificación a " + tokens.size() + " dispositivos...");

        // Iteramos y enviamos uno por uno. Esto usa el endpoint moderno seguro.
        for (String token : tokens) {
            try {
                Message message = Message.builder()
                        .setToken(token)
                        .setNotification(notification)
                        .build();

                String response = FirebaseMessaging.getInstance().send(message);
                System.out.println("Éxito enviando a token [" + token + "]: " + response);

            } catch (Exception e) {
                System.err.println("Error al enviar al token [" + token + "]: " + e.getMessage());
            }
        }
    }
}