package aleosh.online.vivia.features.properties.media.messaging.events;

import aleosh.online.vivia.features.properties.media.domain.entities.MediaUploadSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyMediaValidationSubmitEvent {

    private MediaUploadSession session;
}
