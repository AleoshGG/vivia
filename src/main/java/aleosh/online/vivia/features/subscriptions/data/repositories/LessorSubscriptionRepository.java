package aleosh.online.vivia.features.subscriptions.data.repositories;

import aleosh.online.vivia.features.subscriptions.data.entities.LessorSubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface LessorSubscriptionRepository extends JpaRepository<LessorSubscriptionEntity, UUID> {

    @Query("SELECT s.userId FROM LessorSubscriptionEntity s WHERE s.premiumUntil > :now")
    List<UUID> findActivePremiumUserIds(@Param("now") OffsetDateTime now);
}
