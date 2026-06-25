package aleosh.online.vivia.features.address.neighborhoods.data.repositories;

import aleosh.online.vivia.features.address.neighborhoods.data.entities.NeighborhoodEntity;
import aleosh.online.vivia.features.address.neighborhoods.data.mappers.NeighborhoodMapper;
import aleosh.online.vivia.features.address.neighborhoods.domain.entities.Neighborhood;
import aleosh.online.vivia.features.address.neighborhoods.domain.repositories.INeighborhoodRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class NeighborhoodRepositoryAdapter implements INeighborhoodRepository {

    private final NeighborhoodRepository repository;
    private final NeighborhoodMapper mapper;

    public NeighborhoodRepositoryAdapter(
            NeighborhoodRepository repository,
            @Qualifier("neighborhoodDataMapper") NeighborhoodMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Neighborhood> getNeighborhoodsByCP(String postalCode) {
        List<NeighborhoodEntity> entities = repository.findByPostalCode(postalCode);
        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
