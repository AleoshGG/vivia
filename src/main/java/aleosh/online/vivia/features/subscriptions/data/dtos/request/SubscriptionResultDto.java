package aleosh.online.vivia.features.subscriptions.data.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class SubscriptionResultDto {

    @NotBlank
    private String userId;

    // null indica cancelación/expiración (reservado para uso futuro)
    private OffsetDateTime premiumUntil;
}
