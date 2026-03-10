package aleosh.online.vivia.features.users.lessor.data.repositories;


import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface LessorRepository extends JpaRepository<LessorEntity, UUID> {
    Optional<LessorEntity> findByUsername(String username);
    Optional<LessorEntity> findByCompanyName(String companyName);
    Optional<LessorEntity> findByUserHandle(byte[] userHandle);
}
