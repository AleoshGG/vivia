package aleosh.online.vivia.features.properties.amenity.data.repositories;

import aleosh.online.vivia.features.properties.amenity.data.entities.AmenityEntity;
import aleosh.online.vivia.features.properties.amenity.data.mappers.AmenityMapper;
import aleosh.online.vivia.features.properties.amenity.domain.entities.Amenity;
import aleosh.online.vivia.features.properties.amenity.domain.repositories.IAmenityRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AmenityRepositoryAdapter implements IAmenityRepository {

    private final AmenityRepository repository;
    private final AmenityMapper mapper;

    public AmenityRepositoryAdapter(
            AmenityRepository repository,
            @Qualifier("amenityDataMapper") AmenityMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Amenity> getAll() {
        List<AmenityEntity> entities = repository.findAll();
        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
