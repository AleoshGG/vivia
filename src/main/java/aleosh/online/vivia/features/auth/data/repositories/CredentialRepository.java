package aleosh.online.vivia.features.auth.data.repositories;

import aleosh.online.vivia.features.auth.data.entities.CredentialEntity;
import aleosh.online.vivia.features.auth.domain.objectvalues.CredentialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CredentialRepository extends JpaRepository<CredentialEntity, UUID> {
    Optional<CredentialEntity> findByUserEmailAndCredentialType(String email, CredentialType type);

    Optional<CredentialEntity> findByUser_IdAndCredentialType(UUID userId, CredentialType credentialType);

    @Query("""
        SELECT c FROM CredentialEntity c
        JOIN FETCH c.user u
        LEFT JOIN FETCH u.lessor
        LEFT JOIN FETCH u.lessee
        WHERE c.providerCredentialId = :providerId
        AND c.credentialType = :type
        """)
    Optional<CredentialEntity> findByProviderCredentialIdAndCredentialTypeWithUser(
        @Param("providerId") String providerCredentialId,
        @Param("type") CredentialType credentialType
    );
}
