package aleosh.online.vivia.features.address.address.data.entities;

import aleosh.online.vivia.features.address.neighborhoods.data.entities.NeighborhoodEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "address")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressEntity {

    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", length = 50)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "neighborhood_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private NeighborhoodEntity neighborhood;

    @Column(name = "street", nullable = false, length = 100)
    private String street;

    @Column(name = "exterior_number", nullable = false, length = 10)
    private String exteriorNumber;

    @Column(name = "interior_number", length = 10)
    private String interiorNumber;
}
