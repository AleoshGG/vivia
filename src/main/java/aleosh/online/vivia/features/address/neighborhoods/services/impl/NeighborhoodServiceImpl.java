package aleosh.online.vivia.features.address.neighborhoods.services.impl;

import aleosh.online.vivia.features.address.neighborhoods.data.dtos.response.NeighborhoodResponseDto;
import aleosh.online.vivia.features.address.neighborhoods.domain.entities.Neighborhood;
import aleosh.online.vivia.features.address.neighborhoods.domain.repositories.INeighborhoodRepository;
import aleosh.online.vivia.features.address.neighborhoods.services.INeighborhoodService;
import aleosh.online.vivia.features.address.neighborhoods.services.mappers.NeighborhoodMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NeighborhoodServiceImpl implements INeighborhoodService {

    private final INeighborhoodRepository repository;
    private final NeighborhoodMapper mapper;

    public NeighborhoodServiceImpl(
            INeighborhoodRepository repository,
            @Qualifier("neighborhoodServiceMapper") NeighborhoodMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NeighborhoodResponseDto> getNeighborhoodsByCP(String postalCode) {
        List<Neighborhood> neighborhoods = repository.getNeighborhoodsByCP(postalCode);

        return neighborhoods.stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
