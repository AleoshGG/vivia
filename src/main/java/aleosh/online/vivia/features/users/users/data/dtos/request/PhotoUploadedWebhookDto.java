package aleosh.online.vivia.features.users.users.data.dtos.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PhotoUploadedWebhookDto {
    private String bucket;
    private String key;
    private long size;
}
