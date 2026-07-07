package aleosh.online.vivia.features.properties.media.services;

import aleosh.online.vivia.features.properties.media.data.dtos.request.PropertyMediaManifestItemDto;
import aleosh.online.vivia.features.properties.media.data.dtos.response.PropertyMediaUploadUrlDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IPropertyMediaStorageService {

    List<PropertyMediaUploadUrlDto> generateUploadUrls(UUID sessionId, List<PropertyMediaManifestItemDto> manifest);

    String buildStagingKey(UUID sessionId, String fileKey);

    void deleteObject(String key);

    void deleteBySessionId(UUID sessionId);

    Map<String, String> moveStagingToProperty(UUID sessionId, UUID propertyId);
}
