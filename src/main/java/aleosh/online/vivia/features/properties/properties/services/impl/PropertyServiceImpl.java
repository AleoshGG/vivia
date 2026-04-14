package aleosh.online.vivia.features.properties.properties.services.impl;

import aleosh.online.vivia.features.properties.address.data.entities.AddressEntity;
import aleosh.online.vivia.features.properties.properties.data.dtos.request.CreatePropertyDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyDetailResponseDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyResponseDto;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyEntity;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyImageEntity;
import aleosh.online.vivia.features.properties.properties.data.repositories.PropertyImageRepository;
import aleosh.online.vivia.features.properties.properties.data.repositories.PropertyRepository;
import aleosh.online.vivia.features.properties.properties.services.IPropertyService;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.lessor.data.repositories.LessorRepository;
import aleosh.online.vivia.features.users.lessor.services.IStorageService;
import aleosh.online.vivia.features.users.lessor.services.mappers.LessorMapper;
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
    private final LessorMapper lessorMapper;

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

    @Override
    public PropertyDetailResponseDto getPropertyById(UUID id) {
        PropertyEntity property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Propiedad no encontrada"));
        return mapToDetailResponseDto(property);
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

    private PropertyDetailResponseDto mapToDetailResponseDto(PropertyEntity property) {
        List<String> imageUrls = property.getImages().stream()
                .map(PropertyImageEntity::getUrl)
                .collect(Collectors.toList());

        return PropertyDetailResponseDto.builder()
                .id(property.getId())
                .title(property.getTitle())
                .description(property.getDescription())
                .price(property.getPrice())
                .departmentType(property.getDepartmentType())
                .area(property.getArea())
                .roomsNumber(property.getRoomsNumber())
                .bathroomsNumber(property.getBathroomsNumber())
                .parkingNumber(property.getParkingNumber())
                .lessor(lessorMapper.toLessorResponseDto(property.getLessor()))
                .imageUrls(imageUrls)
                .build();
    }

    @Override
    @Transactional
    public PropertyResponseDto createProperty(CreatePropertyDto dto, String companyName) {
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

        return mapToResponseDto(propertyEntity);
    }

    @Override
    @Transactional
    public PropertyResponseDto uploadImages(UUID propertyId, String companyName, List<MultipartFile> files) {
        PropertyEntity propertyEntity = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Propiedad no encontrada"));

        if (!propertyEntity.getLessor().getCompanyName().equals(companyName)) {
            throw new IllegalArgumentException("No tienes permisos para modificar esta propiedad");
        }

        boolean isFirstTimeImages = propertyEntity.getImages() == null || propertyEntity.getImages().isEmpty();
        boolean uploadedAny = false;

        // 3. Iterar sobre la lista de archivos, subirlos a S3 uno por uno
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        String fileUrl = s3StorageService.uploadFile(file);
                        
                        // 4. Guardar las URLs devueltas en la tabla PropertyImageEntity vinculadas a la propiedad
                        PropertyImageEntity imageEntity = PropertyImageEntity.builder()
                                .url(fileUrl)
                                .property(propertyEntity)
                                .build();
                        propertyImageRepository.save(imageEntity);
                        
                        propertyEntity.addImage(imageEntity);
                        uploadedAny = true;
                    } catch (IOException e) {
                        throw new RuntimeException("Error al subir el archivo a S3", e);
                    }
                }
            }
        }

        if (uploadedAny && isFirstTimeImages) {
            List<String> tokens = lesseeRepository.findByFollowedLessors_Id(propertyEntity.getLessor().getId()).stream()
                    .map(l -> l.getFcmToken())
                    .filter(token -> token != null && !token.isEmpty())
                    .collect(Collectors.toList());

            System.out.println("Tokens encontrados para notificar: " + tokens.size());

            if (!tokens.isEmpty()) {
                notificationService.sendPropertyNotification(tokens, propertyEntity.getLessor().getCompanyName(), propertyEntity.getTitle());
            }
        }

        return mapToResponseDto(propertyEntity);
    }
}
