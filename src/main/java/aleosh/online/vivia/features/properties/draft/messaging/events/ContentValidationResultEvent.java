package aleosh.online.vivia.features.properties.draft.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentValidationResultEvent {

    private UUID draftId;
    private boolean approved;
    private String reason;
}
