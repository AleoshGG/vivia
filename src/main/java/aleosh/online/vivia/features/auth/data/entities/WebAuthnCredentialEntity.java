package aleosh.online.vivia.features.auth.data.entities;

import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "webauthn_credentials")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebAuthnCredentialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", updatable = false, nullable = false, length = 50)
    private UUID id;

    @Column(name = "credential_id", unique = true, nullable = false, length = 512)
    private String credentialId;

    @Column(name = "user_handle", nullable = false, length = 512)
    private String userHandle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JdbcTypeCode(Types.VARCHAR)
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    private UserEntity user;

    @Column(name = "public_key", nullable = false, columnDefinition = "TEXT")
    private String publicKey;

    @Column(name = "sign_count", nullable = false)
    @Builder.Default
    private Long signCount = 0L;

    @Column(name = "aaguid", length = 50)
    private String aaguid;
}
