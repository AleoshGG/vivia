package aleosh.online.vivia.features.properties.draft.services;

import aleosh.online.vivia.features.properties.draft.domain.entities.PropertyDraft;

public interface IAnalysisStorageService {

    void saveRejectedDraft(PropertyDraft draft, String reason);
}
