package aleosh.online.vivia.features.properties.draft.services;

import java.util.UUID;

public interface ICloudinaryAdminService {

    void deleteByDraftId(UUID draftId);
}
