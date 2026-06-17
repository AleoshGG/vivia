package aleosh.online.vivia.features.users.lessor.data.entities;

import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "lessors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessorEntity {

    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "user_id", length = 50)
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    private UserEntity user;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

}
