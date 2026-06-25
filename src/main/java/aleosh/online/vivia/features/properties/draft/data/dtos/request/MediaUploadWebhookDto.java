package aleosh.online.vivia.features.properties.draft.data.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaUploadWebhookDto {

    private String bucket;
    private String key;
    private long size;

    @JsonProperty("eventType")
    private String eventType;
}
