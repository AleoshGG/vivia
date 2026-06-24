package aleosh.online.vivia.features.properties.draft.controllers;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.properties.draft.data.dtos.request.CreatePropertyDraftRequestDto;
import aleosh.online.vivia.features.properties.draft.data.dtos.response.CreatePropertyDraftResponseDto;
import aleosh.online.vivia.features.properties.draft.services.IPropertyDraftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/properties")
@Tag(name = "Property Draft", description = "Endpoints for creating property drafts with presigned URLs for media upload")
public class PropertyDraftController {

    private final IPropertyDraftService propertyDraftService;

    public PropertyDraftController(IPropertyDraftService propertyDraftService) {
        this.propertyDraftService = propertyDraftService;
    }

    @Operation(
            summary = "Create property draft",
            description = "Creates a property draft and returns Cloudinary signed upload parameters for each media file. " +
                    "The client uploads media directly to Cloudinary using these parameters. " +
                    "The draft is stored in Redis with an absolute TTL. Requires authentication as a lessor.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/draft")
    public ResponseEntity<BaseResponse<CreatePropertyDraftResponseDto>> createDraft(
            @Valid @RequestBody CreatePropertyDraftRequestDto request,
            Authentication authentication
    ) {
        // Extract lessorId from JWT
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID lessorId = userDetails.getUserId();

        CreatePropertyDraftResponseDto response = propertyDraftService.createDraft(request, lessorId);

        return new BaseResponse<>(
                true,
                response,
                "Draft creado. Usa los parámetros de 'uploads' para subir los archivos directamente a Cloudinary.",
                HttpStatus.CREATED
        ).buildResponseEntity();
    }
}
