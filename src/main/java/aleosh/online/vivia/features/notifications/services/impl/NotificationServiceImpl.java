package aleosh.online.vivia.features.notifications.services.impl;

import aleosh.online.vivia.features.notifications.services.INotificationService;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
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

            // Usamos sendMulticast en lugar de sendMulticastAsync porque el método ya es @Async
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);

            System.out.println("Notificaciones exitosas: " + response.getSuccessCount());
            System.out.println("Notificaciones fallidas: " + response.getFailureCount());

            // Si hay fallos, inspeccionamos exactamente qué token falló y por qué
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        System.err.println("Error al enviar al token [" + tokens.get(i) + "]: "
                                + responses.get(i).getException().getMessage());
                        System.err.println("Código de error de Firebase: "
                                + responses.get(i).getException().getMessagingErrorCode());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error crítico de comunicación con Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }
}