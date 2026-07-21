package aleosh.online.vivia.features.subscriptions.services.impl;

import aleosh.online.vivia.features.subscriptions.data.entities.LessorSubscriptionEntity;
import aleosh.online.vivia.features.subscriptions.data.repositories.LessorSubscriptionRepository;
import aleosh.online.vivia.features.subscriptions.services.ISubscriptionService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubscriptionServiceImpl implements ISubscriptionService {

    private final LessorSubscriptionRepository repository;

    public SubscriptionServiceImpl(LessorSubscriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void upsertPremium(UUID userId, OffsetDateTime premiumUntil) {
        LessorSubscriptionEntity entity = repository.findById(userId)
                .orElseGet(() -> LessorSubscriptionEntity.builder().userId(userId).build());
        entity.setPremiumUntil(premiumUntil);
        entity.setUpdatedAt(OffsetDateTime.now());
        repository.save(entity);
    }

    @Override
    public boolean isPremiumActive(UUID userId) {
        return repository.findById(userId)
                .map(s -> s.getPremiumUntil() != null
                        && s.getPremiumUntil().isAfter(OffsetDateTime.now()))
                .orElse(false);
    }

    @Override
    public Optional<OffsetDateTime> getPremiumUntil(UUID userId) {
        return repository.findById(userId)
                .map(LessorSubscriptionEntity::getPremiumUntil);
    }
}
