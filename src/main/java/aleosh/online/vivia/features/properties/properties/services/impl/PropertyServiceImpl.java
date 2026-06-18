package aleosh.online.vivia.features.properties.properties.services.impl;

import aleosh.online.vivia.features.properties.properties.data.dtos.request.CreatePropertyDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyDetailResponseDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyResponseDto;
import aleosh.online.vivia.features.properties.properties.services.IPropertyService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class PropertyServiceImpl implements IPropertyService {

    @Override
    public PropertyResponseDto createProperty(CreatePropertyDto dto, String companyName) {
        return null;
    }

    @Override
    public PropertyResponseDto uploadImages(UUID propertyId, String companyName, List<MultipartFile> files) {
        return null;
    }

    @Override
    public List<PropertyResponseDto> getPropertiesByLessorId(UUID lessorId) {
        return null;
    }

    @Override
    public List<PropertyResponseDto> getPropertiesByLessorCompanyName(String companyName) {
        return null;
    }

    @Override
    public void deleteProperty(UUID id, String companyName) {
    }

    @Override
    public Page<PropertyResponseDto> getAllProperties(int page, int size) {
        return null;
    }

    @Override
    public PropertyDetailResponseDto getPropertyById(UUID id) {
        return null;
    }
}