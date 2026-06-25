package aleosh.online.vivia.features.address.neighborhoods.data.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "neighborhood")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NeighborhoodEntity {

    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", length = 50)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "postal_code", length = 5)
    private String postalCode;
}
