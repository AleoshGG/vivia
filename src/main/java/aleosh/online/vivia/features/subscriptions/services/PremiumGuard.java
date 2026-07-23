package aleosh.online.vivia.features.subscriptions.services;

import aleosh.online.vivia.features.properties.properties.data.repositories.PropertyRepository;
import aleosh.online.vivia.features.subscriptions.domain.exceptions.PremiumRequiredException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PremiumGuard {

    private final ISubscriptionService subscriptionService;
    private final PropertyRepository propertyRepository;
    private final int freePropertyLimit;

    public PremiumGuard(
            ISubscriptionService subscriptionService,
            PropertyRepository propertyRepository,
            @Value("${vivia.premium.free-property-limit:2}") int freePropertyLimit
    ) {
        this.subscriptionService = subscriptionService;
        this.propertyRepository = propertyRepository;
        this.freePropertyLimit = freePropertyLimit;
    }

    /**
     * Lanza PremiumRequiredException (402) si el lessor ya alcanzó su límite gratuito.
     * Excluye propiedades con soft-delete para no penalizar por veredictos de reportes.
     */
    public void assertCanPublishProperty(UUID lessorId) {
        if (subscriptionService.isPremiumActive(lessorId)) {
            return;
        }
        long current = propertyRepository.countByLessorIdAndDeletedAtIsNull(lessorId);
        if (current >= freePropertyLimit) {
            throw new PremiumRequiredException(
                    "Alcanzaste el límite gratuito de " + freePropertyLimit
                            + " propiedades. Hazte Premium para publicar más.");
        }
    }

    public boolean isPremium(UUID lessorId) {
        return subscriptionService.isPremiumActive(lessorId);
    }
}
