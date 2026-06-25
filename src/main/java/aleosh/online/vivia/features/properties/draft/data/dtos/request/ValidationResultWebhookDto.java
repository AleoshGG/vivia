package aleosh.online.vivia.features.properties.draft.data.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ValidationResultWebhookDto {

    @NotNull(message = "draftId es obligatorio")
    private UUID draftId;

    private boolean approved;
    private String reason;
}
