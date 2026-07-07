package aleosh.online.vivia.features.properties.media.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyMediaUploadedEvent {

    private UUID sessionId;
    private String fileKey;
    private String storageKey;
    private boolean success;
}
