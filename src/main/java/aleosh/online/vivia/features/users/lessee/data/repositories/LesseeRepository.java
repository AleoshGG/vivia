package aleosh.online.vivia.features.users.lessee.data.repositories;

import aleosh.online.vivia.features.users.lessee.data.entities.LesseeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface LesseeRepository extends JpaRepository<LesseeEntity, UUID> {
    Optional<LesseeEntity> findByUsername(String username);
    Optional<LesseeEntity> findByEmail(String email);
    Optional<LesseeEntity> findByUserHandle(byte[] userHandle);
    List<LesseeEntity> findByFollowedLessors_Id(UUID lessorId);
}