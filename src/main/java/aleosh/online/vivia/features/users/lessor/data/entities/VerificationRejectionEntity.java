package aleosh.online.vivia.features.users.lessor.data.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "verification_rejections")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRejectionEntity {

    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", updatable = false, nullable = false, length = 50)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lessor_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LessorEntity lessor;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "verification_rejection_reasons",
            joinColumns = @JoinColumn(name = "rejection_id")
    )
    @Column(name = "reason", length = 255)
    @Builder.Default
    private List<String> reasons = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
