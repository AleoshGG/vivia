package aleosh.online.vivia.features.properties.draft.services;

import aleosh.online.vivia.features.properties.draft.data.dtos.request.MediaManifestItemDto;
import aleosh.online.vivia.features.properties.draft.data.dtos.response.CloudinaryUploadParamsDto;

import java.util.List;
import java.util.UUID;

public interface ICloudinaryUploadService {

    List<CloudinaryUploadParamsDto> generateSignedUploadParams(UUID draftId, List<MediaManifestItemDto> manifest);

    String buildPublicId(UUID draftId, String fileKey);
}
