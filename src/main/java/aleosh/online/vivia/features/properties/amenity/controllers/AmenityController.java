package aleosh.online.vivia.features.properties.amenity.controllers;

import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.properties.amenity.data.dtos.response.AmenityResponseDto;
import aleosh.online.vivia.features.properties.amenity.services.IAmenityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/amenities")
@Tag(name = "Gestión de Amenidades", description = "Endpoints para consultar amenidades disponibles")
public class AmenityController {

    private final IAmenityService amenityService;

    public AmenityController(IAmenityService amenityService) {
        this.amenityService = amenityService;
    }

    @Operation(
            summary = "Obtener todas las amenidades",
            description = "Devuelve la lista completa de amenidades disponibles en el sistema"
    )
    @GetMapping
    public ResponseEntity<BaseResponse<List<AmenityResponseDto>>> getAll() {
        List<AmenityResponseDto> amenities = amenityService.getAll();

        return new BaseResponse<>(
                true,
                amenities,
                "Amenidades obtenidas exitosamente",
                HttpStatus.OK
        ).buildResponseEntity();
    }
}
