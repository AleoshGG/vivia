package aleosh.online.vivia.features.properties.draft.data.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudinaryUploadParamsDto {

    private String fileKey;
    private String uploadUrl;
    private String apiKey;
    private String signature;
    private long timestamp;
    private String publicId;
    private String folder;
    private String resourceType;
    private int expiresInSeconds;
}
