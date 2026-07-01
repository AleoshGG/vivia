package aleosh.online.vivia.features.users.lessor.services.impl;

import aleosh.online.vivia.features.users.lessor.data.dtos.request.VerificationUploadRequestDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.response.UploadUrlEntryDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.response.VerificationUploadResponseDto;
import aleosh.online.vivia.features.users.lessor.domain.objectvalues.DocumentType;
import aleosh.online.vivia.features.users.lessor.services.IVerificationPresignService;
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
public class VerificationPresignServiceImpl implements IVerificationPresignService {

    private final S3Presigner presigner;
    private final String bucket;
    private final String region;
    private final int expirationMinutes;

    public VerificationPresignServiceImpl(
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
    public VerificationUploadResponseDto generateUploadUrls(UUID lessorId, VerificationUploadRequestDto dto) {
        List<UploadUrlEntryDto> uploads = new ArrayList<>();

        for (DocumentType type : dto.getDocumentTypes()) {
            String key = "verifications/" + lessorId + "/" + type.name();
            String publicUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("image/jpeg")
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(expirationMinutes))
                    .putObjectRequest(putRequest)
                    .build();

            PresignedPutObjectRequest presigned = presigner.presignPutObject(presignRequest);

            uploads.add(new UploadUrlEntryDto(type, presigned.url().toString(), publicUrl));
        }

        return new VerificationUploadResponseDto(uploads, expirationMinutes * 60);
    }
}
