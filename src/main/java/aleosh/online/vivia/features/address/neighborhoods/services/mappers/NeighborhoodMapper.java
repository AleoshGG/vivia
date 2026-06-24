package aleosh.online.vivia.features.address.neighborhoods.services.mappers;

import aleosh.online.vivia.features.address.neighborhoods.data.dtos.response.NeighborhoodResponseDto;
import aleosh.online.vivia.features.address.neighborhoods.domain.entities.Neighborhood;
import org.springframework.stereotype.Component;

@Component("neighborhoodServiceMapper")
public class NeighborhoodMapper {

    public NeighborhoodResponseDto toResponseDto(Neighborhood neighborhood) {
        if (neighborhood == null) {
            return null;
        }

        return NeighborhoodResponseDto.builder()
                .id(neighborhood.getId())
                .name(neighborhood.getName())
                .postalCode(neighborhood.getPostalCode())
                .build();
    }
}
