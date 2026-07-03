package aleosh.online.vivia.features.properties.properties.data.repositories;

import aleosh.online.vivia.features.properties.properties.data.entities.PropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
