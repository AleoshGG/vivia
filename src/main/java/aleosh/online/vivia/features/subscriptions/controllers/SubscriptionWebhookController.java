package aleosh.online.vivia.features.subscriptions.controllers;

import aleosh.online.vivia.features.subscriptions.data.dtos.request.SubscriptionResultDto;
import aleosh.online.vivia.features.subscriptions.services.ISubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/subscriptions")
@Tag(name = "Webhooks internos — Suscripciones",
        description = "Endpoint exclusivo del servicio de pagos. Recibe el premium_until de un lessor. Autenticación por header X-Internal-Api-Key.")
public class SubscriptionWebhookController {

    private final ISubscriptionService subscriptionService;
    private final String internalApiKey;

    public SubscriptionWebhookController(
            ISubscriptionService subscriptionService,
            @Value("${vivia.internal.api-key}") String internalApiKey
    ) {
        this.subscriptionService = subscriptionService;
        this.internalApiKey = internalApiKey;
    }

    @Operation(summary = "Resultado de pago/suscripción (servicio de pagos → Vivia)",
            description = "Hace UPSERT del premium_until del lessor. Requiere header X-Internal-Api-Key.")
    @ApiResponse(responseCode = "200", description = "Estado premium actualizado.")
    @ApiResponse(responseCode = "401", description = "Header X-Internal-Api-Key ausente o incorrecto.")
    @PostMapping("/result")
    public ResponseEntity<Void> result(
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String apiKey,
            @Valid @RequestBody SubscriptionResultDto body
    ) {
        if (!internalApiKey.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        subscriptionService.upsertPremium(UUID.fromString(body.getUserId()), body.getPremiumUntil());
        return ResponseEntity.ok().build();
    }
}
