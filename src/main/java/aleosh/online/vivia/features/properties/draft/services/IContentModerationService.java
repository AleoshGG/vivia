package aleosh.online.vivia.features.properties.draft.services;

import java.util.UUID;

public interface IContentModerationService {

    boolean moderateImage(String s3Key);

    void submitVideoModeration(String s3Key, UUID draftId);
}
