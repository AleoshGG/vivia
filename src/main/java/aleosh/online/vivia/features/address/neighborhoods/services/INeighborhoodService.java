package aleosh.online.vivia.features.address.neighborhoods.services;

import aleosh.online.vivia.features.address.neighborhoods.data.dtos.response.NeighborhoodResponseDto;

import java.util.List;

public interface INeighborhoodService {
    List<NeighborhoodResponseDto> getNeighborhoodsByCP(String postalCode);
}
