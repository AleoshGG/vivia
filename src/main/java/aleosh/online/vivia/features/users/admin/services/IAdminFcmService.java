package aleosh.online.vivia.features.users.admin.services;

import java.util.UUID;

public interface IAdminFcmService {
    void subscribe(UUID adminUserId, String fcmToken);
    void unsubscribe(UUID adminUserId, String fcmToken);
}
