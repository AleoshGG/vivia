package aleosh.online.vivia.features.auth.data.repositories;

import aleosh.online.vivia.features.auth.data.entities.WebAuthnCredentialEntity;
import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebAuthnCredentialRepository extends JpaRepository<WebAuthnCredentialEntity, UUID> {

    Optional<WebAuthnCredentialEntity> findByCredentialId(String credentialId);

    Optional<WebAuthnCredentialEntity> findByUserHandle(String userHandle);

    Optional<WebAuthnCredentialEntity> findByUser_Id(UUID userId);

    List<WebAuthnCredentialEntity> findByUser(UserEntity user);

    @Query("""
        SELECT w FROM WebAuthnCredentialEntity w
        JOIN FETCH w.user u
        LEFT JOIN FETCH u.lessor
        LEFT JOIN FETCH u.lessee
        WHERE w.credentialId = :credentialId
        """)
    Optional<WebAuthnCredentialEntity> findByCredentialIdWithUser(
        @Param("credentialId") String credentialId
    );
}
