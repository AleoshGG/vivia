package aleosh.online.vivia.features.properties.draft.domain.repositories;

import aleosh.online.vivia.features.properties.draft.domain.entities.PropertyDraft;

import java.util.Optional;
import java.util.UUID;

public interface IPropertyDraftRepository {

    PropertyDraft save(PropertyDraft draft);

    Optional<PropertyDraft> getById(UUID id);

    void deleteById(UUID id);

    void updateStatus(UUID draftId, String newStatus);

    int incrementUploadedFiles(UUID draftId);

    int getUploadedFilesCount(UUID draftId);
}
