package aleosh.online.vivia.features.properties.controllers;

import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.properties.data.dtos.request.CreatePropertyDto;
import aleosh.online.vivia.features.properties.data.dtos.response.PropertyResponseDto;
import aleosh.online.vivia.features.properties.services.IPropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "Obtener propiedades por arrendador", description = "Devuelve una lista de propiedades asociadas al ID del arrendador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    })
    @GetMapping(value = "/lessor/{lessorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<PropertyResponseDto>>> getPropertiesByLessor(
            @Parameter(description = "ID del arrendador", required = true)
            @PathVariable UUID lessorId
    ) {
        List<PropertyResponseDto> properties = propertyService.getPropertiesByLessorId(lessorId);
        return new BaseResponse<>(
                true, properties, "Propiedades obtenidas correctamente", HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(summary = "Crear una nueva propiedad", description = "Sube la información de la propiedad junto con sus imágenes a AWS S3.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Propiedad creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<PropertyResponseDto>> createProperty(
            @Parameter(description = "Datos de la propiedad", required = true)
            @RequestPart("property") @Valid CreatePropertyDto createPropertyDto,
            
            @Parameter(description = "Imágenes de la propiedad", required = false)
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        String companyName = SecurityContextHolder.getContext().getAuthentication().getName();
        PropertyResponseDto createdProperty = propertyService.createProperty(createPropertyDto, companyName, images);
        
        return new BaseResponse<>(
                true, createdProperty, "Propiedad creada exitosamente", HttpStatus.CREATED
        ).buildResponseEntity();
    }
}
