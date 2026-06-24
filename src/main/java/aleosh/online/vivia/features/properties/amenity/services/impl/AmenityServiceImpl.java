package aleosh.online.vivia.features.properties.amenity.services.impl;

import aleosh.online.vivia.features.properties.amenity.data.dtos.response.AmenityResponseDto;
import aleosh.online.vivia.features.properties.amenity.domain.entities.Amenity;
import aleosh.online.vivia.features.properties.amenity.domain.repositories.IAmenityRepository;
import aleosh.online.vivia.features.properties.amenity.services.IAmenityService;
import aleosh.online.vivia.features.properties.amenity.services.mappers.AmenityMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AmenityServiceImpl implements IAmenityService {

    private final IAmenityRepository repository;
    private final AmenityMapper mapper;

    public AmenityServiceImpl(
            IAmenityRepository repository,
            @Qualifier("amenityServiceMapper") AmenityMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AmenityResponseDto> getAll() {
        List<Amenity> amenities = repository.getAll();

        return amenities.stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
