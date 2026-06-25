package aleosh.online.vivia.features.properties.draft.services.impl;

import aleosh.online.vivia.features.properties.draft.data.dtos.request.MediaManifestItemDto;
import aleosh.online.vivia.features.properties.draft.data.dtos.response.MediaUploadUrlDto;
import aleosh.online.vivia.features.properties.draft.services.IMediaUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class S3MediaUploadServiceImpl implements IMediaUploadService {

    private final S3Presigner presigner;
    private final String bucket;
    private final int expirationMinutes;

    public S3MediaUploadServiceImpl(
            S3Presigner presigner,
            @Value("${aws.s3.bucket}") String bucket,
            @Value("${aws.s3.presigned-url-expiration-minutes}") int expirationMinutes
    ) {
        this.presigner = presigner;
        this.bucket = bucket;
        this.expirationMinutes = expirationMinutes;
    }

    @Override
    public List<MediaUploadUrlDto> generateUploadUrls(UUID draftId, List<MediaManifestItemDto> manifest) {
        List<MediaUploadUrlDto> result = new ArrayList<>();
        for (MediaManifestItemDto item : manifest) {
            String stagingKey = buildStagingKey(draftId, item.getFileKey());

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(stagingKey)
                    .contentType(item.getContentType())
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(expirationMinutes))
                    .putObjectRequest(putRequest)
                    .build();

            PresignedPutObjectRequest presigned = presigner.presignPutObject(presignRequest);
            int expiresInSeconds = expirationMinutes * 60;

            result.add(new MediaUploadUrlDto(
                    item.getFileKey(),
                    presigned.url().toString(),
                    stagingKey,
                    expiresInSeconds
            ));
        }
        return result;
    }

    @Override
    public String buildStagingKey(UUID draftId, String fileKey) {
        return "media/staging/" + draftId + "/" + fileKey;
    }
}
