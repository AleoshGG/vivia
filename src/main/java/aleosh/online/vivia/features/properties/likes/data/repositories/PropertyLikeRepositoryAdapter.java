package aleosh.online.vivia.features.properties.likes.data.repositories;

import aleosh.online.vivia.features.properties.likes.domain.entities.PropertyLike;
import aleosh.online.vivia.features.properties.likes.domain.repositories.IPropertyLikeRepository;
import aleosh.online.vivia.features.properties.likes.services.mappers.PropertyLikeMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class PropertyLikeRepositoryAdapter implements IPropertyLikeRepository {

    private final PropertyLikeRepository repository;
    private final PropertyLikeMapper mapper;

    public PropertyLikeRepositoryAdapter(PropertyLikeRepository repository, PropertyLikeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public PropertyLike save(PropertyLike like) {
        return mapper.toDomain(repository.save(mapper.toEntity(like)));
    }

    @Override
    @Transactional
    public void deleteByUserIdAndPropertyId(UUID userId, UUID propertyId) {
        repository.deleteByIdUserIdAndIdPropertyId(userId.toString(), propertyId.toString());
    }

    @Override
    public boolean existsByUserIdAndPropertyId(UUID userId, UUID propertyId) {
        return repository.existsByIdUserIdAndIdPropertyId(userId.toString(), propertyId.toString());
    }

    @Override
    public List<PropertyLike> findAllByUserId(UUID userId) {
        return repository.findAllByIdUserId(userId.toString())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
