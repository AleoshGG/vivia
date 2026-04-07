package aleosh.online.vivia.features.properties.properties.domain.repositories;

import aleosh.online.vivia.features.properties.properties.domain.entities.Property;

import java.util.List;
import java.util.UUID;

public interface IPropertyRepository {
    Property save(Property property);
    List<Property> findByLessorId(UUID lessorId);
    void delete(String id);
    List<Property> getAll();
}
