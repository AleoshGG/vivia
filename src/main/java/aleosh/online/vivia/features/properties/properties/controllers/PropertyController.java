package aleosh.online.vivia.features.properties.properties.controllers;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.properties.properties.data.dtos.request.CreatePropertyDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyPreviewResponseDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyResponseDto;
import aleosh.online.vivia.features.properties.properties.services.IPropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/properties")
@Tag(name = "Gestión de Propiedades", description = "Endpoints para CRUD de propiedades en renta")
public class PropertyController {

    private final IPropertyService propertyService;

    public PropertyController(IPropertyService propertyService) {
        this.propertyService = propertyService;
    }

//    @Operation(
//            summary = "Crear propiedad",
//            description = "Registra una nueva propiedad en el sistema. Crea automáticamente la dirección asociada y opcionalmente puede incluir imágenes y videos de la propiedad."
//    )
//    @PostMapping(consumes = "application/json", produces = "application/json")
//    public ResponseEntity<BaseResponse<PropertyResponseDto>> create(
//            @Valid @RequestBody CreatePropertyDto request
//    ) {
//        PropertyResponseDto response = propertyService.create(request);
//
//        return new BaseResponse<>(
//                true,
//                response,
//                "Propiedad creada exitosamente",
//                HttpStatus.CREATED
//        ).buildResponseEntity();
//    }

    @Operation(
            summary = "Obtener propiedad por ID",
            description = "Devuelve los detalles de una propiedad específica incluyendo sus imágenes y videos"
    )
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PropertyResponseDto>> getById(
            @Parameter(description = "ID de la propiedad", required = true)
            @PathVariable UUID id
    ) {
        PropertyResponseDto response = propertyService.getById(id);

        return new BaseResponse<>(
                true,
                response,
                "Propiedad obtenida exitosamente",
                HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(
            summary = "Listar todas las propiedades",
            description = "Devuelve la lista completa de propiedades registradas incluyendo sus imágenes y videos"
    )
    @GetMapping
    public ResponseEntity<BaseResponse<List<PropertyResponseDto>>> getAll() {
        List<PropertyResponseDto> properties = propertyService.getAll();

        return new BaseResponse<>(
                true,
                properties,
                "Propiedades obtenidas exitosamente",
                HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(
            summary = "Obtener propiedad por ID de arrendador",
            description = "Devuelve la propiedad asociada a un arrendador específico incluyendo sus imágenes y videos"
    )
    @GetMapping("/lessor/{lessorId}")
    public ResponseEntity<BaseResponse<PropertyResponseDto>> getByLessorId(
            @Parameter(description = "ID del arrendador", required = true)
            @PathVariable UUID lessorId
    ) {
        PropertyResponseDto response = propertyService.getByLessorId(lessorId);

        return new BaseResponse<>(
                true,
                response,
                "Propiedad obtenida exitosamente",
                HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(
            summary = "Listar mis propiedades",
            description = "Retorna las propiedades del lessor autenticado en formato de vista previa. Acepta un parámetro opcional `limit` para limitar el número de resultados.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('LESSOR')")
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<List<PropertyPreviewResponseDto>>> getMyProperties(
            @Parameter(description = "Número máximo de propiedades a retornar")
            @RequestParam(required = false) Integer limit,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<PropertyPreviewResponseDto> result = propertyService.getMyProperties(userDetails.getUserId(), limit);

        return new BaseResponse<>(
                true,
                result,
                "Propiedades obtenidas exitosamente",
                HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(
            summary = "Eliminar propiedad",
            description = "Elimina una propiedad del sistema junto con todas sus imágenes y videos asociados"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Object>> delete(
            @Parameter(description = "ID de la propiedad", required = true)
            @PathVariable UUID id
    ) {
        propertyService.deleteById(id);

        return new BaseResponse<>(
                true,
                null,
                "Propiedad eliminada exitosamente",
                HttpStatus.OK
        ).buildResponseEntity();
    }
}
