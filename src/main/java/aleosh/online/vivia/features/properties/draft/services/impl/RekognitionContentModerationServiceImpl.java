package aleosh.online.vivia.features.properties.draft.services.impl;

import aleosh.online.vivia.features.properties.draft.services.IContentModerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.util.UUID;

@Service
public class RekognitionContentModerationServiceImpl implements IContentModerationService {

    private static final Logger log = LoggerFactory.getLogger(RekognitionContentModerationServiceImpl.class);

    private final RekognitionClient rekognitionClient;
    private final String bucket;
    private final String snsRoleArn;
    private final String snsTopicArn;
    private final float confidenceThreshold;

    public RekognitionContentModerationServiceImpl(
            RekognitionClient rekognitionClient,
            @Value("${aws.s3.bucket}") String bucket,
            @Value("${aws.rekognition.sns-role-arn}") String snsRoleArn,
            @Value("${aws.rekognition.sns-topic-arn}") String snsTopicArn,
            @Value("${vivia.rekognition.moderation-confidence-threshold}") float confidenceThreshold
    ) {
        this.rekognitionClient = rekognitionClient;
        this.bucket = bucket;
        this.snsRoleArn = snsRoleArn;
        this.snsTopicArn = snsTopicArn;
        this.confidenceThreshold = confidenceThreshold;
    }

    @Override
    public boolean moderateImage(String s3Key) {
        DetectModerationLabelsResponse response = rekognitionClient.detectModerationLabels(
                DetectModerationLabelsRequest.builder()
                        .image(Image.builder()
                                .s3Object(S3Object.builder()
                                        .bucket(bucket)
                                        .name(s3Key)
                                        .build())
                                .build())
                        .minConfidence(confidenceThreshold)
                        .build()
        );

        if (!response.moderationLabels().isEmpty()) {
            log.warn("[Rekognition] Imagen {} contiene contenido inapropiado: {}",
                    s3Key, response.moderationLabels().stream()
                            .map(l -> l.name() + "(" + l.confidence() + ")")
                            .toList());
            return false;
        }

        log.info("[Rekognition] Imagen {} aprobada.", s3Key);
        return true;
    }

    @Override
    public void submitVideoModeration(String s3Key, UUID draftId) {
        StartContentModerationResponse response = rekognitionClient.startContentModeration(
                StartContentModerationRequest.builder()
                        .video(Video.builder()
                                .s3Object(S3Object.builder()
                                        .bucket(bucket)
                                        .name(s3Key)
                                        .build())
                                .build())
                        .notificationChannel(NotificationChannel.builder()
                                .roleArn(snsRoleArn)
                                .snsTopicArn(snsTopicArn)
                                .build())
                        .jobTag(draftId.toString())
                        .minConfidence(confidenceThreshold)
                        .build()
        );

        log.info("[Rekognition] Video {} enviado a análisis async. JobId={}, draftId={}",
                s3Key, response.jobId(), draftId);
    }
}
