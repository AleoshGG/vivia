package aleosh.online.vivia.features.properties.properties.data.repositories;

import aleosh.online.vivia.features.properties.properties.data.entities.PropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<PropertyEntity, UUID> {
    // Las variantes DeletedAtIsNull excluyen propiedades eliminadas por veredicto de reporte;
    // los métodos sin filtro de JpaRepository quedan reservados para el módulo de reportes (admin)
    Optional<PropertyEntity> findByIdAndDeletedAtIsNull(UUID id);
    Optional<PropertyEntity> findByLessorIdAndDeletedAtIsNull(UUID lessorId);
    List<PropertyEntity> findAllByLessorIdAndDeletedAtIsNull(UUID lessorId);
    List<PropertyEntity> findAllByDeletedAtIsNull();
    List<PropertyEntity> findAllByDeletedAtIsNullOrderByCreatedAtDesc();
    List<PropertyEntity> findAllByIdInAndDeletedAtIsNull(Collection<UUID> ids);
    long countByLessorIdAndDeletedAtIsNull(UUID lessorId);

    @Query(value = """
            SELECT p.* FROM properties p
            JOIN address a ON a.id = p.address_id
            WHERE p.deleted_at IS NULL
              AND a.location IS NOT NULL
            ORDER BY a.location <-> CAST(ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326) AS geography)
            LIMIT 5
            """, nativeQuery = true)
    List<PropertyEntity> findNearest(@Param("latitude") BigDecimal latitude, @Param("longitude") BigDecimal longitude);
}
