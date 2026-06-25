package aleosh.online.vivia.features.properties.draft.services;

import java.util.UUID;

public interface IMediaStorageService {

    void deleteByDraftId(UUID draftId);

    void moveStagingToPublic(UUID draftId);
}
