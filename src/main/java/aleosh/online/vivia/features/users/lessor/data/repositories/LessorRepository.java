package aleosh.online.vivia.features.users.lessor.data.repositories;

import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface LessorRepository extends JpaRepository<LessorEntity, UUID> {

    @Modifying
    @Query("UPDATE LessorEntity l SET l.phoneNumber = :phoneNumber WHERE l.id = :lessorId")
    void updatePhoneNumber(@Param("lessorId") UUID lessorId, @Param("phoneNumber") String phoneNumber);
}
