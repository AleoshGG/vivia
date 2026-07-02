package aleosh.online.vivia.features.users.lessor.data.repositories;

import aleosh.online.vivia.features.users.lessor.data.entities.VerificationRejectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VerificationRejectionRepository extends JpaRepository<VerificationRejectionEntity, UUID> {
    Optional<VerificationRejectionEntity> findByLessor_Id(UUID lessorId);
}
