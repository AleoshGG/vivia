package aleosh.online.vivia.features.properties.draft.services.impl;

import aleosh.online.vivia.features.properties.draft.services.ICloudinaryAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
public class CloudinaryAdminServiceImpl implements ICloudinaryAdminService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryAdminServiceImpl.class);

    private static final String[] RESOURCE_TYPES = {"image", "video"};

    private final RestTemplate restTemplate;
    private final String cloudName;
    private final String basicAuthHeader;

    public CloudinaryAdminServiceImpl(
            RestTemplate restTemplate,
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        this.restTemplate = restTemplate;
        this.cloudName = cloudName;
        String credentials = apiKey + ":" + apiSecret;
        this.basicAuthHeader = "Basic " + Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void deleteByDraftId(UUID draftId) {
        String prefix = "drafts/" + draftId;
        for (String resourceType : RESOURCE_TYPES) {
            deleteByPrefix(resourceType, prefix);
        }
    }

    private void deleteByPrefix(String resourceType, String prefix) {
        String url = String.format(
                "https://api.cloudinary.com/v1_1/%s/resources/%s/upload?prefix=%s&invalidate=true",
                cloudName, resourceType, prefix
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, basicAuthHeader);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("Cloudinary devolvió {} al eliminar recursos {}/{}", response.getStatusCode(), resourceType, prefix);
            }
        } catch (Exception e) {
            // No se re-lanza: la eliminación de Cloudinary es best-effort.
            // El draft ya está rechazado independientemente de si los archivos se eliminan.
            log.error("Error eliminando recursos Cloudinary {}/{}: {}", resourceType, prefix, e.getMessage());
        }
    }
}
