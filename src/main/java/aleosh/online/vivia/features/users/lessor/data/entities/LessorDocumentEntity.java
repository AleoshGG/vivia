package aleosh.online.vivia.features.users.lessor.data.entities;

import aleosh.online.vivia.features.users.lessor.domain.objectvalues.DocumentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "lessor_documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessorDocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", updatable = false, nullable = false, length = 50)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lessor_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LessorEntity lessor;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 10)
    private DocumentType documentType;

    @Column(name = "uri", nullable = false, length = 512)
    private String uri;

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private OffsetDateTime uploadedAt;
}
