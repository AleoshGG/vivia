package aleosh.online.vivia.features.properties.draft.messaging.events;

import aleosh.online.vivia.features.properties.draft.domain.entities.PropertyDraft;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyValidationSubmitEvent {

    private PropertyDraft draft;
}
