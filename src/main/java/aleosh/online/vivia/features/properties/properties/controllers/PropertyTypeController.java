package aleosh.online.vivia.features.properties.properties.controllers;

import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyTypeResponseDto;
import aleosh.online.vivia.features.properties.properties.services.IPropertyTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/property-types")
@Tag(name = "Gestión de Tipos de Propiedad", description = "Endpoints para consultar tipos de propiedad")
public class PropertyTypeController {

    private final IPropertyTypeService propertyTypeService;

    public PropertyTypeController(IPropertyTypeService propertyTypeService) {
        this.propertyTypeService = propertyTypeService;
    }

    @Operation(
            summary = "Obtener todos los tipos de propiedad",
            description = "Devuelve la lista completa de tipos de propiedad (Casa, Departamento, Terreno, etc.)"
    )
    @GetMapping
    public ResponseEntity<BaseResponse<List<PropertyTypeResponseDto>>> getAll() {
        List<PropertyTypeResponseDto> propertyTypes = propertyTypeService.getAll();

        return new BaseResponse<>(
                true,
                propertyTypes,
                "Tipos de propiedad obtenidos exitosamente",
                HttpStatus.OK
        ).buildResponseEntity();
    }
}
