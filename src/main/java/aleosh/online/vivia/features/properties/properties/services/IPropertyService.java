package aleosh.online.vivia.features.properties.properties.services;

import aleosh.online.vivia.features.properties.properties.data.dtos.request.CreatePropertyDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyResponseDto;

import java.util.List;
import java.util.UUID;

public interface IPropertyService {
    PropertyResponseDto create(CreatePropertyDto request);
    PropertyResponseDto getById(UUID id);
    List<PropertyResponseDto> getAll();
    void deleteById(UUID id);
    PropertyResponseDto getByLessorId(UUID lessorId);
}
