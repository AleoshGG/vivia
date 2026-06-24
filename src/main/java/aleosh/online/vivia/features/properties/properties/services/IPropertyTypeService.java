package aleosh.online.vivia.features.properties.properties.services;

import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyTypeResponseDto;

import java.util.List;

public interface IPropertyTypeService {
    List<PropertyTypeResponseDto> getAll();
}
