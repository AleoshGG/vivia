package aleosh.online.vivia.features.properties.properties.services.impl;

import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyTypeResponseDto;
import aleosh.online.vivia.features.properties.properties.domain.entities.PropertyType;
import aleosh.online.vivia.features.properties.properties.domain.repositories.IPropertyTypeRepository;
import aleosh.online.vivia.features.properties.properties.services.IPropertyTypeService;
import aleosh.online.vivia.features.properties.properties.services.mappers.PropertyTypeMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyTypeServiceImpl implements IPropertyTypeService {

    private final IPropertyTypeRepository repository;
    private final PropertyTypeMapper mapper;

    public PropertyTypeServiceImpl(
            IPropertyTypeRepository repository,
            @Qualifier("propertyTypeServiceMapper") PropertyTypeMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyTypeResponseDto> getAll() {
        List<PropertyType> propertyTypes = repository.getAll();

        return propertyTypes.stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
