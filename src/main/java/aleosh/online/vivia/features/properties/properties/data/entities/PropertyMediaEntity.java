package aleosh.online.vivia.features.properties.properties.data.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "property_media")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyMediaEntity {

    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", length = 50)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PropertyEntity property;

    @Column(name = "url", nullable = false, length = 512)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private MediaType type;

    @Column(name = "classification", length = 50)
    private String classification;

    public enum MediaType {
        IMAGE,
        VIDEO
    }
}
