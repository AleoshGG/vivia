package aleosh.online.vivia.features.properties.draft.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private UUID userId;
    private String title;
    private String body;
    private Map<String, String> data;
}
