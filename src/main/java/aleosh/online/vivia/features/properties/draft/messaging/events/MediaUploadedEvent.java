package aleosh.online.vivia.features.properties.draft.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaUploadedEvent {

    private UUID draftId;
    private String fileKey;
    private String cloudinaryPublicId;
    private String resourceType;
    private boolean success;
}
