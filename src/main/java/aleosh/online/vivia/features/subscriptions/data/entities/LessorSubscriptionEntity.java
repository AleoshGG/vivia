package aleosh.online.vivia.features.subscriptions.data.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "lessor_subscriptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessorSubscriptionEntity {

    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "user_id", length = 50)
    private UUID userId;

    @Column(name = "premium_until")
    private OffsetDateTime premiumUntil;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
