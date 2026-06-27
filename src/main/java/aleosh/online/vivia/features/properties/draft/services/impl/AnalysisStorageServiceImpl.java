package aleosh.online.vivia.features.properties.draft.services.impl;

import aleosh.online.vivia.features.properties.draft.domain.entities.PropertyDraft;
import aleosh.online.vivia.features.properties.draft.services.IAnalysisStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "firestore.enabled", havingValue = "true")
public class AnalysisStorageServiceImpl implements IAnalysisStorageService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisStorageServiceImpl.class);
    private static final String COLLECTION = "rejected_drafts";

    private final Firestore firestore;
    private final ObjectMapper objectMapper;

    public AnalysisStorageServiceImpl(Firestore firestore, ObjectMapper objectMapper) {
        this.firestore = firestore;
        this.objectMapper = objectMapper;
    }

    @Override
    public void saveRejectedDraft(PropertyDraft draft, String reason) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> draftMap = objectMapper.convertValue(draft, Map.class);
            draftMap.put("rejectionReason", reason);
            draftMap.put("rejectedAt", Instant.now().toString());
            draftMap.put("rejectionType", "ANOMALY");

            ApiFuture<WriteResult> result = firestore
                    .collection(COLLECTION)
                    .document(draft.getId().toString())
                    .set(draftMap);

            result.get(); // esperar confirmación de escritura
            log.info("Draft {} guardado en Firestore (rejected_drafts)", draft.getId());

        } catch (Exception e) {
            log.error("Error guardando draft rechazado {} en Firestore: {}", draft.getId(), e.getMessage(), e);
            // No se re-lanza: la publicación en Firestore no debe bloquear la eliminación del draft de Redis
        }
    }
}
