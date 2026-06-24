package aleosh.online.vivia.features.properties.properties.data.repositories;

import aleosh.online.vivia.features.properties.properties.data.entities.PropertyTypeEntity;
import aleosh.online.vivia.features.properties.properties.data.mappers.PropertyTypeMapper;
import aleosh.online.vivia.features.properties.properties.domain.entities.PropertyType;
import aleosh.online.vivia.features.properties.properties.domain.repositories.IPropertyTypeRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PropertyTypeRepositoryAdapter implements IPropertyTypeRepository {

    private final PropertyTypeRepository repository;
    private final PropertyTypeMapper mapper;

    public PropertyTypeRepositoryAdapter(
            PropertyTypeRepository repository,
            @Qualifier("propertyTypeDataMapper") PropertyTypeMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<PropertyType> getAll() {
        List<PropertyTypeEntity> entities = repository.findAll();
        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
