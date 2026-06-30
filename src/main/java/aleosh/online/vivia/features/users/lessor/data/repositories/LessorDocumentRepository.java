package aleosh.online.vivia.features.users.lessor.data.repositories;

import aleosh.online.vivia.features.users.lessor.data.entities.LessorDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LessorDocumentRepository extends JpaRepository<LessorDocumentEntity, UUID> {
    List<LessorDocumentEntity> findByLessorId(UUID lessorId);
}
