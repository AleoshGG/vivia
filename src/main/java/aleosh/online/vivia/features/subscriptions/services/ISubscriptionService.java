package aleosh.online.vivia.features.subscriptions.services;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ISubscriptionService {

    void upsertPremium(UUID userId, OffsetDateTime premiumUntil);

    boolean isPremiumActive(UUID userId);

    Optional<OffsetDateTime> getPremiumUntil(UUID userId);
}
