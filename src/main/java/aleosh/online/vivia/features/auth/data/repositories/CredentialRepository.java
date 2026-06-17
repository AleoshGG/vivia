package aleosh.online.vivia.features.auth.data.repositories;

import aleosh.online.vivia.features.auth.data.entities.CredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CredentialRepository extends JpaRepository<CredentialEntity, UUID> {
}
