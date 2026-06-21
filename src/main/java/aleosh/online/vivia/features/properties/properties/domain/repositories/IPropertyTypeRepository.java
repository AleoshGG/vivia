package aleosh.online.vivia.features.properties.properties.domain.repositories;

import aleosh.online.vivia.features.properties.properties.domain.entities.PropertyType;
import java.util.List;

public interface IPropertyTypeRepository {
    List<PropertyType> getAll();
}
