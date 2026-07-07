package aleosh.online.vivia.features.users.admin.services.impl;

import aleosh.online.vivia.features.users.admin.domain.exceptions.FcmSubscriptionException;
import aleosh.online.vivia.features.users.admin.services.IAdminFcmService;
import aleosh.online.vivia.features.users.users.data.repositories.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "fcm.enabled", havingValue = "true")
public class AdminFcmServiceImpl implements IAdminFcmService {

    private static final Logger log = LoggerFactory.getLogger(AdminFcmServiceImpl.class);

    // Topics que alimentan el panel admin: verificaciones de identidad y reportes de publicaciones.
    private static final List<String> ADMIN_TOPICS = List.of("admin-verifications", "admin-reports");

    private final UserRepository userRepository;

    public AdminFcmServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void subscribe(UUID adminUserId, String fcmToken) {
        userRepository.updateFcmToken(adminUserId, fcmToken);

        for (String topic : ADMIN_TOPICS) {
            try {
                FirebaseMessaging.getInstance().subscribeToTopic(List.of(fcmToken), topic);
                log.info("Token FCM del admin {} suscrito al topic '{}'", adminUserId, topic);
            } catch (Exception e) {
                log.error("Error suscribiendo token FCM del admin {} al topic '{}': {}",
                        adminUserId, topic, e.getMessage());
                throw new FcmSubscriptionException("No se pudo suscribir el token FCM al topic '" + topic + "'");
            }
        }
    }

    @Override
    @Transactional
    public void unsubscribe(UUID adminUserId, String fcmToken) {
        userRepository.updateFcmToken(adminUserId, null);

        for (String topic : ADMIN_TOPICS) {
            try {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(List.of(fcmToken), topic);
                log.info("Token FCM del admin {} desuscrito del topic '{}'", adminUserId, topic);
            } catch (Exception e) {
                log.error("Error desuscribiendo token FCM del admin {} del topic '{}': {}",
                        adminUserId, topic, e.getMessage());
                throw new FcmSubscriptionException("No se pudo desuscribir el token FCM del topic '" + topic + "'");
            }
        }
    }
}
