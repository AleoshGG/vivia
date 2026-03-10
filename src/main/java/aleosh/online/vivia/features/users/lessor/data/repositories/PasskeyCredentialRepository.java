package aleosh.online.vivia.features.users.lessor.data.repositories;

import aleosh.online.vivia.features.users.lessor.data.entities.PasskeyCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasskeyCredentialRepository extends JpaRepository<PasskeyCredentialEntity, byte[]> {
}