package aleosh.online.vivia.features.properties.properties.domain.repositories;

import aleosh.online.vivia.features.properties.properties.domain.entities.Property;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPropertyRepository {
    Property save(Property property);
    Optional<Property> getById(UUID id);
    List<Property> getAll();
    void deleteById(UUID id);
    Optional<Property> getByLessorId(UUID id);
}
