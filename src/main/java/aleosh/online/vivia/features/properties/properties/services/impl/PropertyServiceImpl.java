package aleosh.online.vivia.features.properties.properties.services.impl;

import aleosh.online.vivia.features.properties.address.data.entities.AddressEntity;
import aleosh.online.vivia.features.properties.properties.data.dtos.request.CreatePropertyDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyResponseDto;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyEntity;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyImageEntity;
import aleosh.online.vivia.features.properties.properties.data.repositories.PropertyImageRepository;
import aleosh.online.vivia.features.properties.properties.data.repositories.PropertyRepository;
import aleosh.online.vivia.features.properties.properties.services.IPropertyService;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.lessor.data.repositories.LessorRepository;
import aleosh.online.vivia.features.users.lessor.services.IStorageService;
import aleosh.online.vivia.features.users.lessee.data.repositories.LesseeRepository;
import aleosh.online.vivia.features.notifications.services.INotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements IPropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final LessorRepository lessorRepository;
    private final IStorageService s3StorageService;
    private final LesseeRepository lesseeRepository;
    private final INotificationService notificationService;

    @Override
    public List<PropertyResponseDto> getPropertiesByLessorId(UUID lessorId) {
        List<PropertyEntity> properties = propertyRepository.findByLessor_Id(lessorId);
        return properties.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Override
    public List<PropertyResponseDto> getPropertiesByLessorCompanyName(String companyName) {
        LessorEntity lessor = lessorRepository.findByCompanyName(companyName)
                .orElseThrow(() -> new IllegalArgumentException("Arrendador no encontrado: " + companyName));
        return getPropertiesByLessorId(lessor.getId());
    }

    @Override
    @Transactional
    public void deleteProperty(UUID id, String companyName) {
        PropertyEntity property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Propiedad no encontrada"));
        
        if (!property.getLessor().getCompanyName().equals(companyName)) {
            throw new IllegalArgumentException("No tienes permisos para eliminar esta propiedad");
        }
        
        propertyRepository.delete(property);
    }

    @Override
    public Page<PropertyResponseDto> getAllProperties(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<PropertyEntity> propertyPage = propertyRepository.findAll(pageRequest);
        return propertyPage.map(this::mapToResponseDto);
    }

    private PropertyResponseDto mapToResponseDto(PropertyEntity property) {
        List<String> imageUrls = property.getImages().stream()
                .map(PropertyImageEntity::getUrl)
                .collect(Collectors.toList());

        aleosh.online.vivia.features.properties.address.data.dtos.AddressDto addressDto = aleosh.online.vivia.features.properties.address.data.dtos.AddressDto.builder()
                .address(property.getAddress().getAddress())
                .city(property.getAddress().getCity())
                .state(property.getAddress().getState())
                .neighborhood(property.getAddress().getNeighborhood())
                .build();

        return PropertyResponseDto.builder()
                .id(property.getId())
                .title(property.getTitle())
                .description(property.getDescription())
                .price(property.getPrice())
                .address(addressDto)
                .departmentType(property.getDepartmentType())
                .area(property.getArea())
                .roomsNumber(property.getRoomsNumber())
                .bathroomsNumber(property.getBathroomsNumber())
                .parkingNumber(property.getParkingNumber())
                .lessorId(property.getLessor().getId())
                .imageUrls(imageUrls)
                .build();
    }

    @Override
    @Transactional
    public PropertyResponseDto createProperty(CreatePropertyDto dto, String companyName, List<MultipartFile> files) {
        // 1. Buscar al Lessor en la base de datos
        LessorEntity lessor = lessorRepository.findByCompanyName(companyName)
                .orElseThrow(() -> new IllegalArgumentException("Arrendador no encontrado: " + companyName));

        AddressEntity addressEntity = AddressEntity.builder()
                .address(dto.getAddress().getAddress())
                .city(dto.getAddress().getCity())
                .state(dto.getAddress().getState())
                .neighborhood(dto.getAddress().getNeighborhood())
                .build();

        // 2. Guardar la PropertyEntity vinculada al Lessor
        PropertyEntity propertyEntity = PropertyEntity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .address(addressEntity)
                .departmentType(dto.getDepartmentType())
                .area(dto.getArea())
                .roomsNumber(dto.getRoomsNumber())
                .bathroomsNumber(dto.getBathroomsNumber())
                .parkingNumber(dto.getParkingNumber())
                .lessor(lessor)
                .build();

        propertyEntity = propertyRepository.save(propertyEntity);

        // 3. Iterar sobre la lista de archivos, subirlos a S3 uno por uno
        List<String> imageUrls = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        String fileUrl = s3StorageService.uploadFile(file);
                        imageUrls.add(fileUrl);
                        
                        // 4. Guardar las URLs devueltas en la tabla PropertyImageEntity vinculadas a la propiedad
                        PropertyImageEntity imageEntity = PropertyImageEntity.builder()
                                .url(fileUrl)
                                .property(propertyEntity)
                                .build();
                        propertyImageRepository.save(imageEntity);
                    } catch (IOException e) {
                        throw new RuntimeException("Error al subir el archivo a S3", e);
                    }
                }
            }
        }

        List<String> tokens = lesseeRepository.findByFollowedLessors_Id(lessor.getId()).stream()
                .map(l -> l.getFcmToken())
                .filter(token -> token != null && !token.isEmpty())
                .collect(Collectors.toList());

        System.out.println("Tokens encontrados para notificar: " + tokens.size());

        if (!tokens.isEmpty()) {
            notificationService.sendPropertyNotification(tokens, lessor.getCompanyName(), propertyEntity.getTitle());
        }

        aleosh.online.vivia.features.properties.address.data.dtos.AddressDto addressDto = aleosh.online.vivia.features.properties.address.data.dtos.AddressDto.builder()
                .address(propertyEntity.getAddress().getAddress())
                .city(propertyEntity.getAddress().getCity())
                .state(propertyEntity.getAddress().getState())
                .neighborhood(propertyEntity.getAddress().getNeighborhood())
                .build();

        return PropertyResponseDto.builder()
                .id(propertyEntity.getId())
                .title(propertyEntity.getTitle())
                .description(propertyEntity.getDescription())
                .price(propertyEntity.getPrice())
                .address(addressDto)
                .departmentType(propertyEntity.getDepartmentType())
                .area(propertyEntity.getArea())
                .roomsNumber(propertyEntity.getRoomsNumber())
                .bathroomsNumber(propertyEntity.getBathroomsNumber())
                .parkingNumber(propertyEntity.getParkingNumber())
                .lessorId(lessor.getId())
                .imageUrls(imageUrls)
                .build();
    }
}
