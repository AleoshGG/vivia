package aleosh.online.vivia.features.users.users.services.impl;

import aleosh.online.vivia.features.users.users.data.dtos.request.PhotoPresignRequestDto;
import aleosh.online.vivia.features.users.users.data.dtos.response.PhotoPresignResponseDto;
import aleosh.online.vivia.features.users.users.domain.exceptions.InvalidPhotoException;
import aleosh.online.vivia.features.users.users.services.IPhotoPresignService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
public class PhotoPresignServiceImpl implements IPhotoPresignService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png");

    private final S3Presigner presigner;
    private final String bucket;
    private final String region;
    private final int expirationMinutes;

    public PhotoPresignServiceImpl(
            S3Presigner presigner,
            @Value("${aws.s3.bucket}") String bucket,
            @Value("${aws.region}") String region,
            @Value("${aws.s3.presigned-url-expiration-minutes}") int expirationMinutes
    ) {
        this.presigner = presigner;
        this.bucket = bucket;
        this.region = region;
        this.expirationMinutes = expirationMinutes;
    }

    @Override
    public PhotoPresignResponseDto generateUploadUrl(UUID userId, PhotoPresignRequestDto dto) {
        if (!ALLOWED_CONTENT_TYPES.contains(dto.getContentType())) {
            throw new InvalidPhotoException("Tipo de contenido no permitido: solo image/jpeg o image/png");
        }

        String key = "profile-photos/" + userId + "/avatar";
        String publicUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(dto.getContentType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expirationMinutes))
                .putObjectRequest(putRequest)
                .build();

        PresignedPutObjectRequest presigned = presigner.presignPutObject(presignRequest);

        return new PhotoPresignResponseDto(
                presigned.url().toString(),
                publicUrl,
                expirationMinutes * 60
        );
    }
}
