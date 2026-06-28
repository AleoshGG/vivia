package aleosh.online.vivia.features.properties.properties.services;

import aleosh.online.vivia.features.properties.properties.data.dtos.request.CreatePropertyDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyDetailResponseDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyMediaResponseDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyPreviewResponseDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyResponseDto;

import java.util.List;
import java.util.UUID;

public interface IPropertyService {
    PropertyResponseDto create(CreatePropertyDto request);
    PropertyDetailResponseDto getDetail(UUID propertyId, UUID userId, boolean isLessee);
    List<PropertyResponseDto> getAll();
    void deleteById(UUID id);
    PropertyResponseDto getByLessorId(UUID lessorId);
    List<PropertyPreviewResponseDto> getMyProperties(UUID lessorId, Integer limit);
    List<PropertyPreviewResponseDto> getSuggestions(Integer limit);
    List<PropertyMediaResponseDto> getMediaByPropertyId(UUID propertyId);
}
