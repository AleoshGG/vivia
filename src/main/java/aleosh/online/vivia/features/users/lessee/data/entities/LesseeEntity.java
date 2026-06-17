package aleosh.online.vivia.features.users.lessee.data.entities;

import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "lessees")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LesseeEntity {

    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "user_id", length = 50)
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "latitude", precision = 12, scale = 9)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 12, scale = 9)
    private BigDecimal longitude;

}
