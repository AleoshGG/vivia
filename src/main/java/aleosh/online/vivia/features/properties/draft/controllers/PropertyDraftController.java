package aleosh.online.vivia.features.properties.draft.controllers;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.properties.draft.data.dtos.request.CreatePropertyDraftRequestDto;
import aleosh.online.vivia.features.properties.draft.data.dtos.response.CreatePropertyDraftResponseDto;
import aleosh.online.vivia.features.properties.draft.services.IPropertyDraftService;
import aleosh.online.vivia.features.subscriptions.services.PremiumGuard;
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
    private final PremiumGuard premiumGuard;

    public PropertyDraftController(IPropertyDraftService propertyDraftService,
                                   PremiumGuard premiumGuard) {
        this.propertyDraftService = propertyDraftService;
        this.premiumGuard = premiumGuard;
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
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID lessorId = userDetails.getUserId();

        premiumGuard.assertCanPublishProperty(lessorId);

        CreatePropertyDraftResponseDto response = propertyDraftService.createDraft(request, lessorId);

        return new BaseResponse<>(
                true,
                response,
                "Draft creado. Usa los parámetros de 'uploads' para subir los archivos directamente a Cloudinary.",
                HttpStatus.CREATED
        ).buildResponseEntity();
    }

    @Operation(
            summary = "Check property publish eligibility",
            description = "Pre-check antes de abrir el formulario de publicación. Responde 200 si el " +
                    "lessor puede publicar; 402 si es free y alcanzó su límite (debe suscribirse).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/posts")
    public ResponseEntity<BaseResponse<Void>> checkCanPublish(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        premiumGuard.assertCanPublishProperty(userDetails.getUserId());

        return new BaseResponse<Void>(
                true,
                null,
                "Puedes publicar una nueva propiedad.",
                HttpStatus.OK
        ).buildResponseEntity();
    }
}
