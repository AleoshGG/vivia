package aleosh.online.vivia.features.subscriptions.data.repositories;

import aleosh.online.vivia.features.subscriptions.data.entities.LessorSubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LessorSubscriptionRepository extends JpaRepository<LessorSubscriptionEntity, UUID> {
}
