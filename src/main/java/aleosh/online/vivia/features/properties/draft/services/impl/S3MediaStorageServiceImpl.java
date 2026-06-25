package aleosh.online.vivia.features.properties.draft.services.impl;

import aleosh.online.vivia.features.properties.draft.services.IMediaStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class S3MediaStorageServiceImpl implements IMediaStorageService {

    private static final Logger log = LoggerFactory.getLogger(S3MediaStorageServiceImpl.class);

    private final S3Client s3Client;
    private final String bucket;

    public S3MediaStorageServiceImpl(
            S3Client s3Client,
            @Value("${aws.s3.bucket}") String bucket
    ) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    @Override
    public void deleteByDraftId(UUID draftId) {
        String prefix = "media/staging/" + draftId + "/";
        try {
            List<ObjectIdentifier> objects = listObjects(prefix);
            if (objects.isEmpty()) {
                return;
            }
            deleteObjects(objects);
            log.info("[S3] Eliminados {} objetos de staging para draftId={}", objects.size(), draftId);
        } catch (Exception e) {
            log.error("[S3] Error eliminando staging para draftId={}: {}", draftId, e.getMessage());
        }
    }

    @Override
    public void moveStagingToPublic(UUID draftId) {
        String stagingPrefix = "media/staging/" + draftId + "/";
        String publicPrefix = "media/public/" + draftId + "/";
        try {
            List<S3Object> objects = listS3Objects(stagingPrefix);
            if (objects.isEmpty()) {
                log.warn("[S3] No se encontraron objetos en staging para draftId={}", draftId);
                return;
            }
            for (S3Object obj : objects) {
                String targetKey = publicPrefix + obj.key().substring(stagingPrefix.length());
                s3Client.copyObject(CopyObjectRequest.builder()
                        .sourceBucket(bucket)
                        .sourceKey(obj.key())
                        .destinationBucket(bucket)
                        .destinationKey(targetKey)
                        .build());
            }
            deleteObjects(objects.stream()
                    .map(o -> ObjectIdentifier.builder().key(o.key()).build())
                    .collect(Collectors.toList()));
            log.info("[S3] Movidos {} objetos de staging a public para draftId={}", objects.size(), draftId);
        } catch (Exception e) {
            log.error("[S3] Error moviendo staging a public para draftId={}: {}", draftId, e.getMessage());
        }
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
