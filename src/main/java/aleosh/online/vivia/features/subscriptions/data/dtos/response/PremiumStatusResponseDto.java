package aleosh.online.vivia.features.subscriptions.data.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PremiumStatusResponseDto {
    private boolean active;
    private OffsetDateTime premiumUntil;
}
