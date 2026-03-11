package aleosh.online.vivia.features.properties.services;

import aleosh.online.vivia.features.properties.data.dtos.request.CreatePropertyDto;
import aleosh.online.vivia.features.properties.data.dtos.response.PropertyResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IPropertyService {
    PropertyResponseDto createProperty(CreatePropertyDto dto, String companyName, List<MultipartFile> files);
    List<PropertyResponseDto> getPropertiesByLessorId(UUID lessorId);
}
