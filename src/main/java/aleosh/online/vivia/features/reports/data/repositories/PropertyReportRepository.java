package aleosh.online.vivia.features.reports.data.repositories;

import aleosh.online.vivia.features.reports.data.entities.PropertyReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PropertyReportRepository extends JpaRepository<PropertyReportEntity, UUID> {

    boolean existsByPropertyIdAndLesseeIdAndIsResolvedFalse(UUID propertyId, UUID lesseeId);

    @Query("SELECT r FROM PropertyReportEntity r WHERE r.isResolved = false ORDER BY r.createdAt DESC")
    List<PropertyReportEntity> findAllPending();

    List<PropertyReportEntity> findAllByLessorIdOrderByCreatedAtDesc(UUID lessorId);
}
