package aleosh.online.vivia.features.users.lessor.data.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationDocumentWebhookDto {
    private String bucket;
    private String key;
    private long size;
}
