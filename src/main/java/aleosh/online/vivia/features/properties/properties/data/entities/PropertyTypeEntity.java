package aleosh.online.vivia.features.properties.properties.data.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "property_type")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyTypeEntity {

    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", length = 50)
    private UUID id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;
}
