package aleosh.online.vivia.features.users.lessor.data.repositories;

import aleosh.online.vivia.features.users.lessor.data.entities.LessorDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LessorDocumentRepository extends JpaRepository<LessorDocumentEntity, UUID> {
    List<LessorDocumentEntity> findByLessorId(UUID lessorId);

    @Modifying
    @Query("DELETE FROM LessorDocumentEntity d WHERE d.lessor.id = :lessorId")
    void deleteByLessorId(@Param("lessorId") UUID lessorId);
}
