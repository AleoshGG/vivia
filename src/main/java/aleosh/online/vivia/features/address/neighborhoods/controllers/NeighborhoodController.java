package aleosh.online.vivia.features.address.neighborhoods.controllers;

import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.address.neighborhoods.data.dtos.response.NeighborhoodResponseDto;
import aleosh.online.vivia.features.address.neighborhoods.services.INeighborhoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/neighborhoods")
@Tag(name = "Gestión de Colonias", description = "Endpoints para consultar colonias por código postal")
@Validated
public class NeighborhoodController {

    private final INeighborhoodService neighborhoodService;

    public NeighborhoodController(INeighborhoodService neighborhoodService) {
        this.neighborhoodService = neighborhoodService;
    }

    @Operation(
            summary = "Obtener colonias por código postal",
            description = "Devuelve la lista de colonias que pertenecen a un código postal específico (5 dígitos)"
    )
    @GetMapping("/{CP}")
    public ResponseEntity<BaseResponse<List<NeighborhoodResponseDto>>> getNeighborhoodsByCP(
            @Parameter(description = "Código postal de 5 dígitos", required = true, example = "29200")
            @PathVariable
            @Pattern(regexp = "^\\d{5}$", message = "El código postal debe tener exactamente 5 dígitos")
            String CP
    ) {
        List<NeighborhoodResponseDto> neighborhoods = neighborhoodService.getNeighborhoodsByCP(CP);

        return new BaseResponse<>(
                true,
                neighborhoods,
                "Colonias obtenidas exitosamente",
                HttpStatus.OK
        ).buildResponseEntity();
    }
}
