package aleosh.online.vivia.features.properties.services.impl;

import aleosh.online.vivia.features.properties.data.dtos.request.CreatePropertyDto;
import aleosh.online.vivia.features.properties.data.dtos.response.PropertyResponseDto;
import aleosh.online.vivia.features.properties.data.entities.PropertyEntity;
import aleosh.online.vivia.features.properties.data.entities.PropertyImageEntity;
import aleosh.online.vivia.features.properties.data.repositories.PropertyImageRepository;
import aleosh.online.vivia.features.properties.data.repositories.PropertyRepository;
import aleosh.online.vivia.features.properties.services.IPropertyService;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.lessor.data.repositories.LessorRepository;
import aleosh.online.vivia.features.users.lessor.services.IStorageService;
import aleosh.online.vivia.features.users.lessee.data.repositories.LesseeRepository;
import aleosh.online.vivia.features.notifications.services.INotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
        return properties.stream().map(property -> {
            List<String> imageUrls = property.getImages().stream()
                    .map(PropertyImageEntity::getUrl)
                    .collect(Collectors.toList());

            return PropertyResponseDto.builder()
                    .id(property.getId())
                    .title(property.getTitle())
                    .description(property.getDescription())
                    .price(property.getPrice())
                    .address(property.getAddress())
                    .city(property.getCity())
                    .state(property.getState())
                    .neighborhood(property.getNeighborhood())
                    .departmentType(property.getDepartmentType())
                    .area(property.getArea())
                    .roomsNumber(property.getRoomsNumber())
                    .bathroomsNumber(property.getBathroomsNumber())
                    .parkingNumber(property.getParkingNumber())
                    .lessorId(property.getLessor().getId())
                    .imageUrls(imageUrls)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PropertyResponseDto createProperty(CreatePropertyDto dto, UUID lessorId, List<MultipartFile> files) {
        // 1. Buscar al Lessor en la base de datos
        LessorEntity lessor = lessorRepository.findById(lessorId)
                .orElseThrow(() -> new IllegalArgumentException("Arrendador no encontrado con ID: " + lessorId));

        // 2. Guardar la PropertyEntity vinculada al Lessor
        PropertyEntity propertyEntity = PropertyEntity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .neighborhood(dto.getNeighborhood())
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

        List<String> tokens = lesseeRepository.findByFollowedLessors_Id(lessorId).stream()
                .map(l -> l.getFcmToken())
                .filter(token -> token != null && !token.isEmpty())
                .collect(Collectors.toList());

        if (!tokens.isEmpty()) {
            notificationService.sendPropertyNotification(tokens, lessor.getCompanyName(), propertyEntity.getTitle());
        }

        return PropertyResponseDto.builder()
                .id(propertyEntity.getId())
                .title(propertyEntity.getTitle())
                .description(propertyEntity.getDescription())
                .price(propertyEntity.getPrice())
                .address(propertyEntity.getAddress())
                .city(propertyEntity.getCity())
                .state(propertyEntity.getState())
                .neighborhood(propertyEntity.getNeighborhood())
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
