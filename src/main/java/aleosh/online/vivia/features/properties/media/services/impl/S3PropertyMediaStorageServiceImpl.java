package aleosh.online.vivia.features.properties.media.services.impl;

import aleosh.online.vivia.features.properties.media.data.dtos.request.PropertyMediaManifestItemDto;
import aleosh.online.vivia.features.properties.media.data.dtos.response.PropertyMediaUploadUrlDto;
import aleosh.online.vivia.features.properties.media.services.IPropertyMediaStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class S3PropertyMediaStorageServiceImpl implements IPropertyMediaStorageService {

    private static final Logger log = LoggerFactory.getLogger(S3PropertyMediaStorageServiceImpl.class);
    private static final String STAGING_PREFIX = "media/property-staging/";
    private static final String PUBLIC_PREFIX = "media/public/";

    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final String bucket;
    private final String region;
    private final int expirationMinutes;

    public S3PropertyMediaStorageServiceImpl(
            S3Client s3Client,
            S3Presigner presigner,
            @Value("${aws.s3.bucket}") String bucket,
            @Value("${aws.region}") String region,
            @Value("${aws.s3.presigned-url-expiration-minutes}") int expirationMinutes
    ) {
        this.s3Client = s3Client;
        this.presigner = presigner;
        this.bucket = bucket;
        this.region = region;
        this.expirationMinutes = expirationMinutes;
    }

    @Override
    public List<PropertyMediaUploadUrlDto> generateUploadUrls(UUID sessionId, List<PropertyMediaManifestItemDto> manifest) {
        List<PropertyMediaUploadUrlDto> result = new ArrayList<>();
        for (PropertyMediaManifestItemDto item : manifest) {
            String stagingKey = buildStagingKey(sessionId, item.getFileKey());

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

            result.add(PropertyMediaUploadUrlDto.builder()
                    .fileKey(item.getFileKey())
                    .uploadUrl(presigned.url().toString())
                    .storageKey(stagingKey)
                    .expiresInSeconds(expirationMinutes * 60)
                    .build());
        }
        return result;
    }

    @Override
    public String buildStagingKey(UUID sessionId, String fileKey) {
        return STAGING_PREFIX + sessionId + "/" + fileKey;
    }

    @Override
    public void deleteObject(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
            log.info("[S3-Media] Objeto eliminado: {}", key);
        } catch (Exception e) {
            log.error("[S3-Media] Error eliminando objeto {}: {}", key, e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteBySessionId(UUID sessionId) {
        String prefix = STAGING_PREFIX + sessionId + "/";
        try {
            List<ObjectIdentifier> objects = listObjects(prefix);
            if (objects.isEmpty()) {
                return;
            }
            deleteObjects(objects);
            log.info("[S3-Media] Eliminados {} objetos de staging para sessionId={}", objects.size(), sessionId);
        } catch (Exception e) {
            log.error("[S3-Media] Error eliminando staging para sessionId={}: {}", sessionId, e.getMessage());
        }
    }

    @Override
    public Map<String, String> moveStagingToProperty(UUID sessionId, UUID propertyId) {
        String stagingPrefix = STAGING_PREFIX + sessionId + "/";
        String publicPrefix = PUBLIC_PREFIX + propertyId + "/" + sessionId + "/";
        Map<String, String> fileKeyToPublicUrl = new LinkedHashMap<>();

        try {
            List<S3Object> objects = listS3Objects(stagingPrefix);
            if (objects.isEmpty()) {
                log.warn("[S3-Media] No se encontraron objetos en staging para sessionId={}", sessionId);
                return fileKeyToPublicUrl;
            }

            for (S3Object obj : objects) {
                String fileKey = obj.key().substring(stagingPrefix.length());
                String targetKey = publicPrefix + fileKey;

                s3Client.copyObject(CopyObjectRequest.builder()
                        .sourceBucket(bucket)
                        .sourceKey(obj.key())
                        .destinationBucket(bucket)
                        .destinationKey(targetKey)
                        .build());

                String publicUrl = buildPublicUrl(targetKey);
                fileKeyToPublicUrl.put(fileKey, publicUrl);
            }

            deleteObjects(objects.stream()
                    .map(o -> ObjectIdentifier.builder().key(o.key()).build())
                    .collect(Collectors.toList()));

            log.info("[S3-Media] Movidos {} objetos de staging a public para sessionId={}, propertyId={}",
                    objects.size(), sessionId, propertyId);
        } catch (Exception e) {
            log.error("[S3-Media] Error moviendo staging a public para sessionId={}: {}", sessionId, e.getMessage());
        }

        return fileKeyToPublicUrl;
    }

    private String buildPublicUrl(String key) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }

    private List<S3Object> listS3Objects(String prefix) {
        return s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix)
                .build()).contents();
    }

    private List<ObjectIdentifier> listObjects(String prefix) {
        return listS3Objects(prefix).stream()
                .map(o -> ObjectIdentifier.builder().key(o.key()).build())
                .collect(Collectors.toList());
    }

    private void deleteObjects(List<ObjectIdentifier> objects) {
        s3Client.deleteObjects(DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(Delete.builder().objects(objects).build())
                .build());
    }
}
