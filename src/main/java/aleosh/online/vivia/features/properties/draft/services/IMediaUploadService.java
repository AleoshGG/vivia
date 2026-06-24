package aleosh.online.vivia.features.properties.draft.services;

import aleosh.online.vivia.features.properties.draft.data.dtos.request.MediaManifestItemDto;
import aleosh.online.vivia.features.properties.draft.data.dtos.response.MediaUploadUrlDto;

import java.util.List;
import java.util.UUID;

public interface IMediaUploadService {

    List<MediaUploadUrlDto> generateUploadUrls(UUID draftId, List<MediaManifestItemDto> manifest);

    String buildStagingKey(UUID draftId, String fileKey);
}
