package aleosh.online.vivia.features.properties.draft.services.impl;

import aleosh.online.vivia.features.properties.draft.domain.entities.PropertyDraft;
import aleosh.online.vivia.features.properties.draft.services.IAnalysisStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpAnalysisStorageServiceImpl implements IAnalysisStorageService {

    private static final Logger log = LoggerFactory.getLogger(NoOpAnalysisStorageServiceImpl.class);

    @Override
    public void saveRejectedDraft(PropertyDraft draft, String reason) {
        log.warn("[NO-OP] saveRejectedDraft ignorado (Firestore no configurado): draftId={}, reason={}",
                draft.getId(), reason);
    }
}
