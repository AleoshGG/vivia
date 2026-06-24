package aleosh.online.vivia.features.properties.draft.data.dtos.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ValidationResultWebhookDto {

    private UUID draftId;
    private boolean approved;
    private String reason;
}
