package aleosh.online.vivia.features.properties.draft.services.impl;

import aleosh.online.vivia.features.properties.draft.data.dtos.request.MediaManifestItemDto;
import aleosh.online.vivia.features.properties.draft.data.dtos.response.CloudinaryUploadParamsDto;
import aleosh.online.vivia.features.properties.draft.services.ICloudinaryUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CloudinaryUploadServiceImpl implements ICloudinaryUploadService {

    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;
    private final String uploadBaseUrl;
    private final int expiresInSeconds;

    public CloudinaryUploadServiceImpl(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret,
            @Value("${cloudinary.upload-url}") String uploadBaseUrl,
            @Value("${cloudinary.signed-upload-expiration-seconds}") int expiresInSeconds
    ) {
        this.cloudName = cloudName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.uploadBaseUrl = uploadBaseUrl;
        this.expiresInSeconds = expiresInSeconds;
    }

    @Override
    public List<CloudinaryUploadParamsDto> generateSignedUploadParams(UUID draftId, List<MediaManifestItemDto> manifest) {
        long timestamp = Instant.now().getEpochSecond();
        String folder = "drafts/" + draftId;

        List<CloudinaryUploadParamsDto> result = new ArrayList<>();
        for (MediaManifestItemDto item : manifest) {
            String publicId = buildPublicId(draftId, item.getFileKey());
            String resourceType = resolveResourceType(item.getContentType());
            String signature = generateSignature(publicId, folder, timestamp);
            String uploadUrl = uploadBaseUrl + resourceType + "/upload";

            result.add(new CloudinaryUploadParamsDto(
                    item.getFileKey(),
                    uploadUrl,
                    apiKey,
                    signature,
                    timestamp,
                    publicId,
                    folder,
                    resourceType,
                    expiresInSeconds
            ));
        }
        return result;
    }

    @Override
    public String buildPublicId(UUID draftId, String fileKey) {
        return "drafts/" + draftId + "/" + fileKey;
    }

    private String generateSignature(String publicId, String folder, long timestamp) {
        // Cloudinary signature: SHA1 of "folder=X&public_id=Y&timestamp=Z" + apiSecret
        String paramsToSign = String.format("folder=%s&public_id=%s&timestamp=%d", folder, publicId, timestamp);
        String toHash = paramsToSign + apiSecret;
        return sha1Hex(toHash);
    }

    private String resolveResourceType(String contentType) {
        if (contentType.startsWith("video/")) {
            return "video";
        }
        return "image";
    }

    private String sha1Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 no disponible", e);
        }
    }
}
