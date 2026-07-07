package aleosh.online.vivia.features.properties.media.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyMediaValidationResultEvent {

    private UUID sessionId;
    private boolean approved;
    private String reason;
}
