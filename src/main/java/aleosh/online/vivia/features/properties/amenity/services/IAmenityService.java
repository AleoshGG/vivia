package aleosh.online.vivia.features.properties.amenity.services;

import aleosh.online.vivia.features.properties.amenity.data.dtos.response.AmenityResponseDto;

import java.util.List;

public interface IAmenityService {
    List<AmenityResponseDto> getAll();
}
