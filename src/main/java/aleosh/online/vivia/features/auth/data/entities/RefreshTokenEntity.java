package aleosh.online.vivia.features.auth.data.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(length = 50)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "user_identifier", nullable = false)
    private String userIdentifier;

    @Column(nullable = false)
    private String role;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;
}
