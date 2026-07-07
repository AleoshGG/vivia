package aleosh.online.vivia.features.properties.media.data.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyMediaUploadWebhookDto {

    private String bucket;
    private String key;
    private long size;
}
