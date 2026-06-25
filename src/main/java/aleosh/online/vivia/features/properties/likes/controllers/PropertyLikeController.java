package aleosh.online.vivia.features.properties.likes.controllers;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.properties.likes.IPropertyLikeService;
import aleosh.online.vivia.features.properties.likes.data.dtos.request.ToggleLikeRequestDto;
import aleosh.online.vivia.features.properties.likes.data.dtos.response.ToggleLikeResponseDto;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyPreviewResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/properties")
@Tag(name = "Property Likes", description = "Gestión de propiedades favoritas")
public class PropertyLikeController {

    private final IPropertyLikeService propertyLikeService;

    public PropertyLikeController(IPropertyLikeService propertyLikeService) {
        this.propertyLikeService = propertyLikeService;
    }

    @Operation(
            summary = "Dar o quitar like a una propiedad",
            description = "Toggle: si la propiedad ya está en favoritos la elimina, si no la agrega.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/me/likes")
    public ResponseEntity<BaseResponse<ToggleLikeResponseDto>> toggleLike(
            @Valid @RequestBody ToggleLikeRequestDto request,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        boolean liked = propertyLikeService.toggleLike(userDetails.getUserId(), request.getPropertyId());

        String message = liked ? "Propiedad agregada a favoritos" : "Propiedad eliminada de favoritos";
        return new BaseResponse<>(true, new ToggleLikeResponseDto(liked), message, HttpStatus.OK)
                .buildResponseEntity();
    }

    @Operation(
            summary = "Obtener propiedades favoritas",
            description = "Retorna la lista de propiedades que el usuario autenticado ha marcado como favoritas.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/me/likes")
    public ResponseEntity<BaseResponse<List<PropertyPreviewResponseDto>>> getMyLikes(
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<PropertyPreviewResponseDto> result = propertyLikeService.getMyLikes(userDetails.getUserId());

        return new BaseResponse<>(true, result, "Favoritos obtenidos exitosamente", HttpStatus.OK)
                .buildResponseEntity();
    }
}
