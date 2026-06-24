package aleosh.online.vivia.features.properties.draft.data.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CloudinaryWebhookPayloadDto {

    @JsonProperty("public_id")
    private String publicId;

    @JsonProperty("notification_type")
    private String notificationType;

    @JsonProperty("resource_type")
    private String resourceType;

    @JsonProperty("secure_url")
    private String secureUrl;

    @JsonProperty("bytes")
    private Long bytes;

    @JsonProperty("created_at")
    private String createdAt;
}
