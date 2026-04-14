package aleosh.online.vivia.core.config.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.credentials.session-token:}")
    private String sessionToken;

    @Value("${cloud.aws.region.static:us-east-1}")
    private String region;

    @Bean
    public S3Client s3Client() {
        AwsCredentialsProvider credentialsProvider;

        if (sessionToken != null && !sessionToken.isEmpty()) {
            credentialsProvider = StaticCredentialsProvider.create(
                    AwsSessionCredentials.create(accessKey, secretKey, sessionToken));
        } else {
            credentialsProvider = StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey));
        }

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}

