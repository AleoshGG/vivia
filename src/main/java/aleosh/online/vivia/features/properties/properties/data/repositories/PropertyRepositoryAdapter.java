package aleosh.online.vivia.features.properties.properties.data.repositories;

import aleosh.online.vivia.features.properties.properties.data.entities.PropertyEntity;
import aleosh.online.vivia.features.properties.properties.data.mappers.PropertyMapper;
import aleosh.online.vivia.features.properties.properties.domain.entities.Property;
import aleosh.online.vivia.features.properties.properties.domain.exceptions.PropertyNotFoundException;
import aleosh.online.vivia.features.properties.properties.domain.repositories.IPropertyRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class PropertyRepositoryAdapter implements IPropertyRepository {

    private final PropertyRepository repository;
    private final PropertyMapper mapper;

    public PropertyRepositoryAdapter(
            PropertyRepository repository,
            @Qualifier("propertyDataMapper") PropertyMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Property save(Property property) {
        PropertyEntity entity = mapper.toEntity(property);
        PropertyEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Property> getById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Property> getAll() {
        List<PropertyEntity> entities = repository.findAll();
        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        if (!repository.existsById(id)) {
            throw new PropertyNotFoundException("Property not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public Optional<Property> getByLessorId(UUID id) {
        return repository.findByLessorId(id)
                .map(mapper::toDomain);
    }
}
