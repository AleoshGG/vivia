package aleosh.online.vivia.features.auth.data.repositories;

import aleosh.online.vivia.features.auth.data.entities.CredentialEntity;
import aleosh.online.vivia.features.auth.domain.objectvalues.CredentialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CredentialRepository extends JpaRepository<CredentialEntity, UUID> {
    Optional<CredentialEntity> findByUserEmailAndCredentialType(String email, CredentialType type);
}
