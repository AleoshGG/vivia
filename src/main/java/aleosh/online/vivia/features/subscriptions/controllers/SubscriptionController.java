package aleosh.online.vivia.features.subscriptions.controllers;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.subscriptions.data.dtos.response.PremiumStatusResponseDto;
import aleosh.online.vivia.features.subscriptions.services.ISubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/subscriptions")
@Tag(name = "Suscripciones", description = "Estado premium del lessor autenticado.")
public class SubscriptionController {

    private final ISubscriptionService subscriptionService;

    public SubscriptionController(ISubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Operation(summary = "Estado premium del lessor autenticado",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<PremiumStatusResponseDto>> me(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID lessorId = userDetails.getUserId();

        boolean active = subscriptionService.isPremiumActive(lessorId);
        OffsetDateTime premiumUntil = subscriptionService.getPremiumUntil(lessorId).orElse(null);

        PremiumStatusResponseDto dto = new PremiumStatusResponseDto(active, premiumUntil);

        return new BaseResponse<>(true, dto, "Estado premium consultado.", HttpStatus.OK)
                .buildResponseEntity();
    }
}
