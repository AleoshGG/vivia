package aleosh.online.vivia.features.properties.amenity.data.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "amenity")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmenityEntity {

    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", length = 50)
    private UUID id;

    @Column(name = "name", nullable = false, length = 80)
    private String name;
}
