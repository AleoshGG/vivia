package aleosh.online.vivia.features.reports.data.entities;

import aleosh.online.vivia.features.properties.properties.data.entities.PropertyEntity;
import aleosh.online.vivia.features.users.lessee.data.entities.LesseeEntity;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "property_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyReportEntity {

    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", length = 50)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PropertyEntity property;

    @Column(name = "property_title", nullable = false, length = 200)
    private String propertyTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lessor_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LessorEntity lessor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lessee_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LesseeEntity lessee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ReportReasonEntity reason;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "is_resolved", nullable = false)
    @Builder.Default
    private boolean isResolved = false;

    @Column(name = "verdict", length = 50)
    private String verdict;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity resolvedBy;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "resolved_at")
    private Instant resolvedAt;
}
