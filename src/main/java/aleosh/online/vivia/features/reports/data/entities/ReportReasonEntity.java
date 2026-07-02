package aleosh.online.vivia.features.reports.data.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "report_reasons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportReasonEntity {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "priority", nullable = false, length = 20)
    private String priority;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
