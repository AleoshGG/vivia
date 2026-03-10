package aleosh.online.vivia.core.config.aws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {

    @Bean
    public S3Client s3Client() {
        // El cliente por defecto busca automáticamente las variables de entorno:
        // AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY y AWS_SESSION_TOKEN
        return S3Client.builder()
                .region(Region.US_EAST_1) // Asegúrate que coincida con tu bucket
                .build();
    }
}

