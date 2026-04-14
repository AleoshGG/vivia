package aleosh.online.vivia.features.properties.properties.controllers;

import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.properties.properties.data.dtos.request.CreatePropertyDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyDetailResponseDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyResponseDto;
import aleosh.online.vivia.features.properties.properties.services.IPropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/properties")
@RequiredArgsConstructor
@Tag(name = "Gestión de Propiedades", description = "Endpoints para crear y gestionar propiedades.")
public class PropertyController {

    private final IPropertyService propertyService;

    @Operation(summary = "Obtener todas las propiedades paginadas", description = "Devuelve una lista de todas las propiedades con paginación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Page<PropertyResponseDto>>> getAllProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<PropertyResponseDto> properties = propertyService.getAllProperties(page, size);
        return new BaseResponse<>(
                true, properties, "Propiedades obtenidas correctamente", HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(summary = "Obtener detalle de una propiedad", description = "Devuelve el detalle de una propiedad y la información del arrendador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Propiedad obtenida correctamente"),
            @ApiResponse(responseCode = "404", description = "Propiedad no encontrada", content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<PropertyDetailResponseDto>> getPropertyById(@PathVariable UUID id) {
        PropertyDetailResponseDto property = propertyService.getPropertyById(id);
        return new BaseResponse<>(
                true, property, "Propiedad obtenida correctamente", HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(summary = "Eliminar propiedad", description = "Elimina una propiedad y todos sus datos relacionados si pertenece al arrendador autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Propiedad eliminada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o sin permisos", content = @Content)
    })
    @PreAuthorize("hasRole('LESSOR')")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> deleteProperty(@PathVariable UUID id) {
        String companyName = SecurityContextHolder.getContext().getAuthentication().getName();
        propertyService.deleteProperty(id, companyName);
        return new BaseResponse<Void>(
                true, null, "Propiedad eliminada exitosamente", HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(summary = "Obtener propiedades por arrendador", description = "Devuelve una lista de propiedades asociadas al arrendador autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    })
    @PreAuthorize("hasRole('LESSOR')")
    @GetMapping(value = "/lessor", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<PropertyResponseDto>>> getPropertiesByLessor() {
        String companyName = SecurityContextHolder.getContext().getAuthentication().getName();
        List<PropertyResponseDto> properties = propertyService.getPropertiesByLessorCompanyName(companyName);
        return new BaseResponse<>(
                true, properties, "Propiedades obtenidas correctamente", HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(summary = "Crear una nueva propiedad", description = "Crea la información base de la propiedad sin imágenes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Propiedad creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PreAuthorize("hasRole('LESSOR')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<PropertyResponseDto>> createProperty(
            @Parameter(description = "Datos de la propiedad", required = true)
            @RequestBody @Valid CreatePropertyDto createPropertyDto
    ) {
        String companyName = SecurityContextHolder.getContext().getAuthentication().getName();
        PropertyResponseDto createdProperty = propertyService.createProperty(createPropertyDto, companyName);
        
        return new BaseResponse<>(
                true, createdProperty, "Propiedad creada exitosamente", HttpStatus.CREATED
        ).buildResponseEntity();
    }

    @Operation(summary = "Subir imágenes a la propiedad", description = "Sube imágenes a una propiedad existente y notifica a los seguidores si es la primera vez.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imágenes subidas exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o propiedad no encontrada", content = @Content)
    })
    @PreAuthorize("hasRole('LESSOR')")
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<PropertyResponseDto>> uploadImages(
            @Parameter(description = "ID de la propiedad", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Imágenes de la propiedad", required = true)
            @RequestPart("images") List<MultipartFile> images
    ) {
        String companyName = SecurityContextHolder.getContext().getAuthentication().getName();
        PropertyResponseDto updatedProperty = propertyService.uploadImages(id, companyName, images);

        return new BaseResponse<>(
                true, updatedProperty, "Imágenes subidas exitosamente", HttpStatus.OK
        ).buildResponseEntity();
    }
}
