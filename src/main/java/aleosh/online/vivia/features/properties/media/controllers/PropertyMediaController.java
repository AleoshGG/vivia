package aleosh.online.vivia.features.properties.media.controllers;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.properties.media.data.dtos.request.AddPropertyMediaDto;
import aleosh.online.vivia.features.properties.media.data.dtos.request.ChangeMainImageDto;
import aleosh.online.vivia.features.properties.media.data.dtos.response.MediaUploadSessionResponseDto;
import aleosh.online.vivia.features.properties.media.services.IPropertyMediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/properties/media")
@Tag(name = "Property Media", description = "Gestión de medios de propiedades publicadas")
@SecurityRequirement(name = "bearerAuth")
public class PropertyMediaController {

    private final IPropertyMediaService propertyMediaService;

    public PropertyMediaController(IPropertyMediaService propertyMediaService) {
        this.propertyMediaService = propertyMediaService;
    }

    @Operation(description = "Elimina un medio de la propiedad; primero lo borra de S3 y después de la base de datos en una sola operación transaccional.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LESSOR')")
    public ResponseEntity<Void> deleteMedia(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        UUID lessorId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        propertyMediaService.deleteMedia(id, lessorId);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Cambia la imagen principal de la propiedad: la imagen MAIN actual pasa a OTHER y la nueva imagen indicada pasa a MAIN.")
    @PatchMapping
    @PreAuthorize("hasRole('LESSOR')")
    public ResponseEntity<BaseResponse<Void>> changeMainImage(
            @Valid @RequestBody ChangeMainImageDto dto,
            Authentication authentication
    ) {
        UUID lessorId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        propertyMediaService.changeMainImage(dto, lessorId);
        BaseResponse<Void> response = new BaseResponse<>(true, null, "Imagen principal actualizada correctamente", HttpStatus.OK);
        return response.buildResponseEntity();
    }

    @Operation(description = "Agrega medios a una propiedad publicada generando URLs prefirmadas de S3 y disparando la moderación de contenido asíncrona.")
    @PostMapping
    @PreAuthorize("hasRole('LESSOR')")
    public ResponseEntity<BaseResponse<MediaUploadSessionResponseDto>> addMedia(
            @Valid @RequestBody AddPropertyMediaDto dto,
            Authentication authentication
    ) {
        UUID lessorId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        MediaUploadSessionResponseDto result = propertyMediaService.addMedia(dto, lessorId);
        BaseResponse<MediaUploadSessionResponseDto> response = new BaseResponse<>(
                true,
                result,
                "Sesión de subida creada. Usa las URLs de 'uploads' para subir los archivos directamente a S3.",
                HttpStatus.CREATED
        );
        return response.buildResponseEntity();
    }
}
