package aleosh.online.vivia.features.users.lessor.data.repositories;

import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.lessor.domain.objectvalues.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LessorRepository extends JpaRepository<LessorEntity, UUID> {

    @Modifying
    @Query("UPDATE LessorEntity l SET l.phoneNumber = :phoneNumber WHERE l.id = :lessorId")
    void updatePhoneNumber(@Param("lessorId") UUID lessorId, @Param("phoneNumber") String phoneNumber);

    @Modifying
    @Query("UPDATE LessorEntity l SET l.verificationStatus = :status WHERE l.id = :lessorId")
    void updateVerificationStatus(@Param("lessorId") UUID lessorId, @Param("status") VerificationStatus status);

    List<LessorEntity> findByVerificationStatus(VerificationStatus status);
}
